package org.mfk.tools.sqlprint.core.impl;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.mfk.tools.sqlprint.core.AbstractStatementSql;
import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.utils.MethodProxy;
import org.mfk.tools.sqlprint.core.utils.RefactUtils;


public class Kingbase8StatementSqlImpl extends AbstractStatementSql implements IStatementSql {
    static Object NULL_OBJECT = null;
    public  static MethodProxy byteConverter= null;
    public  static MethodProxy utils= null;
    public  static MethodProxy kBbox= null;
    public  static MethodProxy kBpoint= null;
    public  static MethodProxy uuidArrayAssistant;
    protected Object preparedParameters = null;
    Object[] paramValues = null;

    public Kingbase8StatementSqlImpl(Statement statement, Object otherparams) throws Exception {

        super(statement, otherparams);
        if (byteConverter==null){
            byteConverter=new MethodProxy("com.kingbase8.util.ByteConverter");
            byteConverter.addMethod("int2","int2",new Class[]{byte[].class,int.class});
            byteConverter.addMethod("int4","int4",new Class[]{byte[].class,int.class});
            byteConverter.addMethod("int8","int8",new Class[]{byte[].class,int.class});
            byteConverter.addMethod("float4","float4",new Class[]{byte[].class,int.class});
            byteConverter.addMethod("float8","float8",new Class[]{byte[].class,int.class});
            utils=new MethodProxy("com.kingbase8.core.Utils");
            utils.addMethod("escapeLiteral","escapeLiteral",new Class[]{StringBuilder.class,String.class,boolean.class});
            kBbox = new MethodProxy("com.kingbase8.geometric.KBbox");
            kBbox.addMethod("setByteValue","setByteValue",new Class[]{byte[].class,int.class});
            kBpoint = new MethodProxy("com.kingbase8.geometric.KBpoint");
            kBpoint.addMethod("setByteValue","setByteValue",new Class[]{byte[].class,int.class});
            uuidArrayAssistant= new MethodProxy("com.kingbase8.jdbc.UUIDArrayAssistant");
            uuidArrayAssistant.addMethod("buildElement","buildElement",new Class[]{byte[].class, int.class,int.class});
        }
        if (statement.getClass().getName().equals("com.kingbase8.jdbc.KbStatement")){
            try {
                sql = RefactUtils.getFieldValue(otherparams, "key.sql").toString();
            } catch (Exception e) {
                sql = RefactUtils.getFieldValue(otherparams, "_key.sqlString").toString();
            }

        }else {
            Object binds = null;
            boolean hs_ = false;
            try {
                sql = RefactUtils.getFieldValue(statement, "preparedQuery.key").toString();
                preparedParameters = RefactUtils.getFieldValue(statement, "preparedParameters");
                binds = RefactUtils.getFieldValue(preparedParameters, "paramValues");
            }catch (Exception e){
                hs_ = true;
                sql = RefactUtils.getFieldValue(statement, "preparedQuery._key").toString();
                preparedParameters = RefactUtils.getFieldValue(statement, "preparedParameterList");
                binds = RefactUtils.getFieldValue(preparedParameters, "_paramValues");
            }
            // sql赋值

            // 拼接字段赋值

            if (binds != null) {
                paramValues = (Object[]) binds;
                int count = Array.getLength(binds);
                for (int i = 0; i < count; i++) {
                    Object obj = Array.get(binds, i);
                    try {
                        obj = getValue(i, false,hs_);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    String val = converObjectSplicingString(obj);
                    splicingParameters.add(val);
                }
            }
        }
    }

    protected String converDateToSqlString(Date date) {
        return "to_date('" + defaultDateFormat.format(date) + "','yyyy-mm-dd hh24:mi:ss')";
    }

    public Object getValue(int index, boolean standardConformingStrings,boolean hs_) throws Exception {

        String pre="";
        if (hs_) {
            pre="_";
        }
        int[] paramTypes = (int[]) RefactUtils.getFieldValue(this.preparedParameters, pre+"paramTypes");
        byte[] flags = (byte[]) RefactUtils.getFieldValue(this.preparedParameters, pre+"flags");

        if (NULL_OBJECT == null) {
            NULL_OBJECT = RefactUtils.getFieldValue(this.preparedParameters, "NULL_OBJECT");
        }

        if (paramValues[index] == null) {
            return "?";
        }
        if (paramValues[index] == NULL_OBJECT) {
            return null;
        }
        if ((flags[index] & 4) == 4) {
            switch (paramTypes[index]) {
                case 21: {
                    short s = byteConverter.invoke(short.class,"int2", (Object) paramValues[index],0);
                    return s;
                }
                case 23: {
                    int i = byteConverter.invoke(int.class,"int4", (Object) paramValues[index],0);
                    return i;
                }
                case 20: {
                    long l = byteConverter.invoke(long.class,"int8", (Object) paramValues[index],0);
                    return l;
                }
                case 700: {
                    float f = byteConverter.invoke(float.class,"float4", (Object) paramValues[index],0);
                    if (Float.isNaN(f)) {
                        return "'NaN'::real";
                    }
                    return f;
                }
                case 701: {
                    double d = byteConverter.invoke(double.class,"float8", (Object) paramValues[index],0);

                    if (Double.isNaN(d)) {
                        return "'NaN'::double precision";
                    }
                    return d;
                }
                case 2950: {
                    String uuid = uuidArrayAssistant.invoke(uuidArrayAssistant.newInstanceObj(),Object.class,"buildElement",paramValues[index], 0, 16).toString();
                    return "" + uuid + "::uuid";
                }
                case 600: {
                    Object obj=kBpoint.newInstanceObj();
                    kBpoint.invoke(obj,"setByteValue",paramValues[index], 0);
                    return "" + obj.toString() + "::point";
                }
                case 603: {
                    Object obj=kBbox.newInstanceObj();
                    kBbox.invoke(obj,"setByteValue",paramValues[index], 0);
                    return "" + obj.toString() + "::box";
                }
            }
            return "?";
        }
        String param = paramValues[index].toString();
        StringBuilder p = new StringBuilder(3 + (param.length() + 10) / 10 * 11);
        p.append("");
        try {
            p =utils.invoke(StringBuilder.class,"escapeLiteral",(StringBuilder) p, (String) param, (boolean) standardConformingStrings);
        } catch (SQLException sqle) {
            p.append(param);
        }
        p.append("");
        int paramType = paramTypes[index];
        if (paramType == 1114) {
            p.append("::timestamp");
        } else if (paramType == 1184) {
            p.append("::timestamp with time zone");
        } else if (paramType == 1083) {
            p.append("::time");
        } else if (paramType == 1266) {
            p.append("::time with time zone");
        } else if (paramType == 1082) {
            p.append("::date");
        } else if (paramType == 1186) {
            p.append("::interval");
        } else if (paramType == 1700) {
            p.append("::numeric");
        }
        return p.toString();
    }
}
