package org.mfk.tools.sqlprint.core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

public class BasicFormatterImpl {
	private static final Set BEGIN_CLAUSES = new HashSet();
	private static final Set END_CLAUSES = new HashSet();
	private static final Set LOGICAL = new HashSet();
	private static final Set QUANTIFIERS = new HashSet();
	private static final Set DML = new HashSet();
	private static final Set MISC = new HashSet();
	static final String indentString = "    ";
	static final String initial = "\n    ";

	static {
		BEGIN_CLAUSES.add("left");
		BEGIN_CLAUSES.add("right");
		BEGIN_CLAUSES.add("inner");
		BEGIN_CLAUSES.add("outer");
		BEGIN_CLAUSES.add("group");
		BEGIN_CLAUSES.add("order");
		END_CLAUSES.add("where");
		END_CLAUSES.add("set");
		END_CLAUSES.add("having");
		END_CLAUSES.add("join");
		END_CLAUSES.add("from");
		END_CLAUSES.add("by");
		END_CLAUSES.add("join");
		END_CLAUSES.add("into");
		END_CLAUSES.add("union");
		LOGICAL.add("and");
		LOGICAL.add("or");
		LOGICAL.add("when");
		LOGICAL.add("else");
		LOGICAL.add("end");
		QUANTIFIERS.add("in");
		QUANTIFIERS.add("all");
		QUANTIFIERS.add("exists");
		QUANTIFIERS.add("some");
		QUANTIFIERS.add("any");
		DML.add("insert");
		DML.add("update");
		DML.add("delete");
		MISC.add("select");
		MISC.add("on");
	}

	public String format(String source) {
		return new FormatProcess(source).perform().trim();
	}

	private static class FormatProcess {
		boolean beginLine = true;
		boolean afterBeginBeforeEnd = false;
		boolean afterByOrSetOrFromOrSelect = false;
		boolean afterValues = false;
		boolean afterOn = false;
		boolean afterBetween = false;
		boolean afterInsert = false;
		int inFunction = 0;
		int parensSinceSelect = 0;
		private LinkedList parenCounts = new LinkedList();
		private LinkedList afterByOrFromOrSelects = new LinkedList();
		int indent = 1;
		StringBuffer result = new StringBuffer();
		StringTokenizer tokens;
		String lastToken;
		String token;
		String lcToken;

		public FormatProcess(String sql) {
			this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
		}

		public String perform() {
			this.result.append(BasicFormatterImpl.initial);
			while (this.tokens.hasMoreTokens()) {
				String t;
				this.token = this.tokens.nextToken();
				this.lcToken = this.token.toLowerCase();
				if ("'".equals(this.token)) {
					do {
						t = this.tokens.nextToken();
						this.token = String.valueOf(this.token) + t;
					} while (!"'".equals(t) && this.tokens.hasMoreTokens());
				} else if ("\"".equals(this.token)) {
					do {
						t = this.tokens.nextToken();
						this.token = String.valueOf(this.token) + t;
					} while (!"\"".equals(t));
				}
				if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token)) {
					this.commaAfterByOrFromOrSelect();
				} else if (this.afterOn && ",".equals(this.token)) {
					this.commaAfterOn();
				} else if ("(".equals(this.token)) {
					this.openParen();
				} else if (")".equals(this.token)) {
					this.closeParen();
				} else if (BEGIN_CLAUSES.contains(this.lcToken)) {
					this.beginNewClause();
				} else if (END_CLAUSES.contains(this.lcToken)) {
					this.endNewClause();
				} else if ("select".equals(this.lcToken)) {
					this.select();
				} else if (DML.contains(this.lcToken)) {
					this.updateOrInsertOrDelete();
				} else if ("values".equals(this.lcToken)) {
					this.values();
				} else if ("on".equals(this.lcToken)) {
					this.on();
				} else if (this.afterBetween && this.lcToken.equals("and")) {
					this.misc();
					this.afterBetween = false;
				} else if (LOGICAL.contains(this.lcToken)) {
					this.logical();
				} else if (FormatProcess.isWhitespace(this.token)) {
					this.white();
				} else {
					this.misc();
				}
				if (FormatProcess.isWhitespace(this.token))
					continue;
				this.lastToken = this.lcToken;
			}
			return this.result.toString();
		}

		private void commaAfterOn() {
			this.out();
			--this.indent;
			this.newline();
			this.afterOn = false;
			this.afterByOrSetOrFromOrSelect = true;
		}

		private void commaAfterByOrFromOrSelect() {
			this.out();
			this.newline();
		}

		private void logical() {
			if ("end".equals(this.lcToken)) {
				--this.indent;
			}
			this.newline();
			this.out();
			this.beginLine = false;
		}

		private void on() {
			++this.indent;
			this.afterOn = true;
			this.newline();
			this.out();
			this.beginLine = false;
		}

		private void misc() {
			this.out();
			if ("between".equals(this.lcToken)) {
				this.afterBetween = true;
			}
			if (this.afterInsert) {
				this.newline();
				this.afterInsert = false;
			} else {
				this.beginLine = false;
				if ("case".equals(this.lcToken)) {
					++this.indent;
				}
			}
		}

		private void white() {
			if (!this.beginLine) {
				this.result.append(" ");
			}
		}

		private void updateOrInsertOrDelete() {
			this.out();
			++this.indent;
			this.beginLine = false;
			if ("update".equals(this.lcToken)) {
				this.newline();
			}
			if ("insert".equals(this.lcToken)) {
				this.afterInsert = true;
			}
		}

		private void select() {
			this.out();
			++this.indent;
			this.newline();
			this.parenCounts.addLast(new Integer(this.parensSinceSelect));
			this.afterByOrFromOrSelects.addLast(this.afterByOrSetOrFromOrSelect);
			this.parensSinceSelect = 0;
			this.afterByOrSetOrFromOrSelect = true;
		}

		private void out() {
			this.result.append(this.token);
		}

		private void endNewClause() {
			if (!this.afterBeginBeforeEnd) {
				--this.indent;
				if (this.afterOn) {
					--this.indent;
					this.afterOn = false;
				}
				this.newline();
			}
			this.out();
			if (!"union".equals(this.lcToken)) {
				++this.indent;
			}
			this.newline();
			this.afterBeginBeforeEnd = false;
			this.afterByOrSetOrFromOrSelect = "by".equals(this.lcToken) || "set".equals(this.lcToken)
					|| "from".equals(this.lcToken);
		}

		private void beginNewClause() {
			if (!this.afterBeginBeforeEnd) {
				if (this.afterOn) {
					--this.indent;
					this.afterOn = false;
				}
				--this.indent;
				this.newline();
			}
			this.out();
			this.beginLine = false;
			this.afterBeginBeforeEnd = true;
		}

		private void values() {
			--this.indent;
			this.newline();
			this.out();
			++this.indent;
			this.newline();
			this.afterValues = true;
		}

		private void closeParen() {
			--this.parensSinceSelect;
			if (this.parensSinceSelect < 0) {
				--this.indent;
				this.parensSinceSelect = (Integer) this.parenCounts.removeLast();
				this.afterByOrSetOrFromOrSelect = (Boolean) this.afterByOrFromOrSelects.removeLast();
			}
			if (this.inFunction > 0) {
				--this.inFunction;
				this.out();
			} else {
				if (!this.afterByOrSetOrFromOrSelect) {
					--this.indent;
					this.newline();
				}
				this.out();
			}
			this.beginLine = false;
		}

		private void openParen() {
			if (FormatProcess.isFunctionName(this.lastToken) || this.inFunction > 0) {
				++this.inFunction;
			}
			this.beginLine = false;
			if (this.inFunction > 0) {
				this.out();
			} else {
				this.out();
				if (!this.afterByOrSetOrFromOrSelect) {
					++this.indent;
					this.newline();
					this.beginLine = true;
				}
			}
			++this.parensSinceSelect;
		}

		private static boolean isFunctionName(String tok) {
			boolean isIdentifier;
			char begin = tok.charAt(0);
			boolean bl = isIdentifier = Character.isJavaIdentifierStart(begin) || '\"' == begin;
			if (!(!isIdentifier || LOGICAL.contains(tok) || END_CLAUSES.contains(tok) || QUANTIFIERS.contains(tok)
					|| DML.contains(tok) || MISC.contains(tok))) {
				return true;
			}
			return false;
		}

		private static boolean isWhitespace(String token) {
			if (" \n\r\f\t".indexOf(token) >= 0) {
				return true;
			}
			return false;
		}

		private void newline() {
			this.result.append("\n");
			int i = 0;
			while (i < this.indent) {
				this.result.append(BasicFormatterImpl.indentString);
				++i;
			}
			this.beginLine = true;
		}
	}

}