package test.junit.data;

import java.util.Date;
import java.util.Random;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;

import org.junit.Assert;
import org.junit.Test;

/**
 * jUnit test created to test the refactoring of VAttribute. 
 * 
 * @author P.T. de Boer. 
 */
public class testVAttribute 
{
    final static Random rnd=new Random(0); 

    private String createRandomString(int size)
    {
        StringBuffer strbuf=new StringBuffer(size);
        for (int i=0;i<size;i++)
        {
            int val=rnd.nextInt();
            char c=(char)('a'+val%31); 
            strbuf.append(c); 
        }
        
        String str=strbuf.toString(); 
        return str; 
    }
    
    private StringList createRandomStringList(int listSize,int stringSize)
    {
        String array[]=new String[listSize]; 
        for (int i=0;i<listSize;i++)
            array[i]=createRandomString(stringSize);
         
        return new StringList(array); 
    }


    
    // VAttributeType ::= {BOOLEAN,INT,LONG,FLOAT,DOUBLE,STRING,ENUM,VRL,TIME} 
    
    class TestValues
    {
        boolean  boolval=true; 
        int      intval=1; 
        long     longval=1024*1024*1024;
        float    floatval=1.13f; 
        double   doubleval=Math.PI;  
        String    strval="String Value"; 
        String    enumstrs[]={"aap","noot","mies"}; 
        String    datestr="1970-01-13 01:23:45.678"; 
        VRL       vrl=new VRL("file:","user","localhost",-1,"/tmp/stuff/","query","fragment"); 
        Date      dateval=Presentation.createDateFromNormalizedDateTimeString(datestr); 
                
        TestValues()
        {
            
        }
        
        TestValues(boolean _boolval,
                   int _intval,
                   long _longval, 
                   float _floatval, 
                   double _doubleval,
                   String _strval,
                   String[] _enumvals,
                   VRL _vrl,
                   Date _dateval)
        {
            this.boolval=_boolval;
            this.intval=_intval; 
            this.longval=_longval; 
            this.floatval=_floatval; 
            this.doubleval=_doubleval; 
            this.strval=_strval; 
            this.enumstrs=_enumvals;
            this.vrl=_vrl; 
            this.dateval=_dateval; 
            this.datestr=Presentation.createNormalizedDateTimeString(_dateval);
        }
    }
    
    protected void setUp()
    {
    }

    // Tears down the tests fixture. (Called after every tests case method.)
    protected void tearDown()
    {
    }
    
    @Test
    public void testPresentationDateTimeString() 
    {
        // text exception: 
        //testPresentationDateTimeString("000000-00-00 00:00:00.000");
        testPresentationDateTimeString("0001-01-01 00:00:00.000");
        testPresentationDateTimeString("1970-01-13 01:23:45.678");  
        testPresentationDateTimeString("999999-12-31 23:59:59.999");  
    }
    
    public void testPresentationDateTimeString(String datestr)
    {
        Date date=Presentation.createDateFromNormalizedDateTimeString(datestr);
        String reversestr=Presentation.createNormalizedDateTimeString(date); 
        Assert.assertEquals("Normalized datetime strings should be the same",datestr,reversestr); 
    }
    
    @Test 
    public void testValueConstructors()
    {
        TestValues tValues=new TestValues(); 
        testValueConstructors(tValues); 
        
        tValues=new TestValues(
                true,
                (int)1, 
                (long)1024*1024*1024,
                (float)1.13f, 
                (double)Math.PI,
                "String Value",     
                new String[]{"aap","noot","mies"},
                new VRL("file:","user","localhost",-1,"/tmp/stuff/","query","fragment"),    
                Presentation.createDateFromNormalizedDateTimeString("1970-01-13 01:23:45.678")
                );
        testValueConstructors(tValues); 

        // empty: neutral (legal) 
        tValues=new TestValues(
                false,
                (int)0, 
                (long)0,
                (float)0, 
                (double)0,
                "",     
                new String[]{""},
                new VRL("ref:",null,null,0,"",null,null),    
                Presentation.createDateFromNormalizedDateTimeString("0001-01-01 00:00:00.000")
                );
        testValueConstructors(tValues); 

        // minimum/null (allowed) 
        tValues=new TestValues(
                false,
                (int)Integer.MIN_VALUE, 
                (long)Long.MIN_VALUE,
                (float)Float.MIN_VALUE, 
                (double)Double.MIN_VALUE,
                null,     
                new String[]{null},
                new VRL(null,null,null,0,null,null,null),    
                Presentation.createDate(1)
                );
        
        testValueConstructors(tValues); 
        
        // max (allowed) 
        tValues=new TestValues(
                true,
                (int)Integer.MAX_VALUE, 
                (long)Long.MAX_VALUE,
                (float)Float.MAX_VALUE, 
                (double)Double.MAX_VALUE,
                createRandomString(1024),     
                createRandomStringList(1024,1024).toArray(),
                new VRL("scheme:",
                        "Jan.Piet.Joris[Groupid-dev-0]",
                        "www.llanfairpwllgwyngyllgogerychwyrndrobwyll-llantysiliogogogoch.com",
                        99999,
                        "/tmp/llanfairpwllgwyngyllgogerychwyrndrobwyll-llantysiliogogogoch/With Space",
                        "aap=AapValue&noot=NootValue&mies=MiewValue",
                        "fragment"), 
                        Presentation.createDateFromNormalizedDateTimeString("0001-01-01 00:00:00.000")
                );
        
        testValueConstructors(tValues); 
    }

  
    public void testValueConstructors(TestValues tValues)
    {
        double epsd=Double.MIN_NORMAL; 
        float epsf=Float.MIN_NORMAL; 
        
        // Core Types 
        VAttribute boolAttr=new VAttribute("boolname",tValues.boolval); 
        Assert.assertEquals("boolean value doesn't match",tValues.boolval,boolAttr.getBooleanValue());
        Assert.assertEquals("boolean type expected",VAttributeType.BOOLEAN,boolAttr.getType()); 
        VAttribute intAttr=new VAttribute("intname",tValues.intval); 
        Assert.assertEquals("int value doesn't match",tValues.intval,intAttr.getIntValue());
        Assert.assertEquals("int type expected",VAttributeType.INT,intAttr.getType()); 
        VAttribute longAttr=new VAttribute("longname",tValues.longval); 
        Assert.assertEquals("long value doesn't match",tValues.longval,longAttr.getLongValue());
        Assert.assertEquals("long type expected",VAttributeType.LONG,longAttr.getType()); 
        VAttribute floatAttr=new VAttribute("floatname",tValues.floatval); 
        Assert.assertEquals("float value doesn't match",tValues.floatval,floatAttr.getFloatValue(),epsf);
        Assert.assertEquals("float type expected",VAttributeType.FLOAT,floatAttr.getType()); 
        VAttribute doubleAttr=new VAttribute("doublename",tValues.doubleval); 
        Assert.assertEquals("double value doesn't match",tValues.doubleval,doubleAttr.getDoubleValue(),epsd);
        Assert.assertEquals("double type expected",VAttributeType.DOUBLE,doubleAttr.getType()); 
        VAttribute strAttr=new VAttribute("strname",tValues.strval); 
        Assert.assertEquals("String value doesn't match",tValues.strval,strAttr.getStringValue());
        Assert.assertEquals("String type expected",VAttributeType.STRING,strAttr.getType());
        for (int i=0;i<tValues.enumstrs.length;i++)
        {
            VAttribute enumAttr=new VAttribute("enumname",tValues.enumstrs,tValues.enumstrs[i]); 
            Assert.assertEquals("Enum value #"+i+" doesn't match",tValues.enumstrs[i],enumAttr.getStringValue());
            Assert.assertEquals("Enum type expected",VAttributeType.ENUM,enumAttr.getType()); 
        }
        
        // uses Presentation class for Date <-> String conversion ! 
        VAttribute dateAttr=new VAttribute("datename",tValues.dateval); 
        Assert.assertEquals("Date value doesn't match",tValues.datestr,dateAttr.getStringValue());
        Assert.assertEquals("Date type expected",VAttributeType.TIME,dateAttr.getType());
      
    }
    
    @Test 
    public void testNullConstructors()
    {
        // NULL value with NULL type default to String
        VAttribute attr=new VAttribute((String)null,(String)null);
        Assert.assertEquals("NULL attribute should return NULL",null,attr.getStringValue());  
        Assert.assertEquals("NULL value defaults to StringType",VAttributeType.STRING, attr.getType());
    }
 
    @Test 
    public void testStringValueConstructors() throws VRLSyntaxException
    {   
        // test simple String based constructors and match against object value 
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","true",new Boolean(true));
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","false",new Boolean(false));
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","True",new Boolean(true));
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","False",new Boolean(false));
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","TRUE",new Boolean(true));
        testStringValueConstructor(VAttributeType.BOOLEAN,"name","FALSE",new Boolean(false));
        //
        testStringValueConstructor(VAttributeType.INT,"name1","0", new Integer(0));
        testStringValueConstructor(VAttributeType.INT,"name2","1", new Integer(1));
        testStringValueConstructor(VAttributeType.INT,"name3","-1", new Integer(-1));
        testStringValueConstructor(VAttributeType.INT,"name4",""+Integer.MAX_VALUE,new Integer(Integer.MAX_VALUE));  
        testStringValueConstructor(VAttributeType.INT,"name4",""+Integer.MIN_VALUE,new Integer(Integer.MIN_VALUE));  
        testStringValueConstructor(VAttributeType.LONG,"name1","0",new Long(0));
        testStringValueConstructor(VAttributeType.LONG,"name2","1",new Long(1));
        testStringValueConstructor(VAttributeType.LONG,"name3","-1",new Long(-1));
        testStringValueConstructor(VAttributeType.LONG,"name4",""+Long.MAX_VALUE,new Long(Long.MAX_VALUE));  
        testStringValueConstructor(VAttributeType.LONG,"name5",""+Long.MIN_VALUE,new Long(Long.MIN_VALUE));  
        // watch out for rounding errors from decimal to IEEE floats/doubles !
        testStringValueConstructor(VAttributeType.FLOAT,"name","0.0",new Float(0.0));
        testStringValueConstructor(VAttributeType.FLOAT,"name","1.0",new Float(1.0));
        testStringValueConstructor(VAttributeType.FLOAT,"name","-1.0",new Float(-1.0));
        testStringValueConstructor(VAttributeType.FLOAT,"name",""+Float.MAX_VALUE,new Float(Float.MAX_VALUE));
        testStringValueConstructor(VAttributeType.FLOAT,"name",""+Float.MIN_VALUE,new Float(Float.MIN_VALUE));
        // todo: check rounding errors
        testStringValueConstructor(VAttributeType.DOUBLE,"name","0.0",new Double(0.0));
        testStringValueConstructor(VAttributeType.DOUBLE,"name","1.0",new Double(1.0));
        testStringValueConstructor(VAttributeType.DOUBLE,"name","-1.0",new Double(-1.0));
        testStringValueConstructor(VAttributeType.DOUBLE,"name","-1.123456",new Double(-1.123456));
        testStringValueConstructor(VAttributeType.DOUBLE,"name",""+Double.MAX_VALUE,new Double(Double.MAX_VALUE));
        testStringValueConstructor(VAttributeType.DOUBLE,"name",""+Double.MIN_VALUE,new Double(Double.MIN_VALUE));
        // STRING
        testStringValueConstructor(VAttributeType.STRING,"name","value","value");
        testStringValueConstructor(VAttributeType.STRING,"name","","");

        // DATETIME
        long millies=System.currentTimeMillis(); 
        Date dateVal=Presentation.createDate(millies); 
        testStringValueConstructor(VAttributeType.TIME,"name",Presentation.createNormalizedDateTimeString(millies),dateVal);  
        // VRL 
        String vrlStr="file://user@host.domain:1234/Directory/A File/";
        VRL vrl=new VRL(vrlStr);
        testStringValueConstructor(VAttributeType.VRL,"name","file://user@host.domain:1234/Directory/A File/",vrl);
               
    }
    
    public void testStringValueConstructor(VAttributeType type,String name,String value,Object objectValue) 
    {
        // basic constructor tests 
        VAttribute attr=VAttribute.createFromString(type,name,value);
        // check type,name and String value 
        Assert.assertEquals("Type must be:"+type,type,attr.getType());
        Assert.assertEquals("String values should match (if parsed correctly) for type:"+type,value,attr.getStringValue()); 
        Assert.assertEquals("Attribute name must match",name,attr.getName()); 
        Assert.assertTrue("isType() must return true for attr:"+attr,attr.isType(type)); 
        
        testObjectValue(attr,objectValue); 
    }

    // test whether object has matching type and native value 
    public void testObjectValue(VAttribute attr,Object objValue)
    {
        VAttributeType type=getObjectVAttributeType(objValue);
        Assert.assertTrue("Object type must be:"+type,attr.isType(type)); 

        // check native value type! 
        switch(type)
        {
            case BOOLEAN:
                Assert.assertTrue("getBoolValue() must match native type!",(attr.getBooleanValue()==((Boolean)objValue))); 
                break;
            case INT:
                Assert.assertTrue("getIntValue() must match native type!",(attr.getIntValue()==((Integer)objValue))); 
                break;
            case LONG:
                Assert.assertTrue("getLongValue() must match native type!",(attr.getLongValue()==((Long)objValue))); 
                break; 
            case FLOAT:
                Assert.assertTrue("getFloatValue() must match native type!",(attr.getFloatValue()==((Float)objValue))); 
                break; 
            case DOUBLE:
                Assert.assertTrue("getDoubleValue() must match native type!",(attr.getDoubleValue()==((Double)objValue))); 
                break; 
            case VRL:
                try
                {
                    Assert.assertTrue("getDoubleValue() must match native type!",((VRL)objValue).equals(attr.getVRL()) );
                }
                catch (Exception e)
                {
                    Assert.fail("Exception:"+e);
                } 
                break; 
            case TIME:
                Assert.assertTrue("getDateValue() must match native type!", compareDateValues(attr.getDateValue(),(Date)objValue)); 
                break; 

        }
        
        //compareDateValues(attr.getDateValue(),(Date)objValue))); 
    }

    private boolean compareDateValues(Date val1,Date val2)
    {
        if (val1==val2)
            return true;
        
        if ( val1.toString().equals(val2.toString()) ) 
            return true; 
        
        return false; 
    }

    public VAttributeType getObjectVAttributeType(Object obj)
    { 
        if (obj instanceof Boolean)
            return VAttributeType.BOOLEAN;
        else if (obj instanceof Integer)
            return VAttributeType.INT;
        else if (obj instanceof Long)
            return VAttributeType.LONG;
        else if (obj instanceof Float)
            return VAttributeType.FLOAT;
        else if (obj instanceof Double)
            return VAttributeType.DOUBLE;
        else if (obj instanceof String)
            return VAttributeType.STRING;
        else if (obj instanceof Date)
            return VAttributeType.TIME;
        else if (obj instanceof VRL)
            return VAttributeType.VRL;
        else if (obj instanceof Enum)
            return VAttributeType.ENUM;
        
        // check enum ? 
        return null; 
    }
    
    @Test
    public void testVRLVAttribute() throws VRLSyntaxException
    {
        String vrlstr="file://user@host.domain:1234/Directory/A File/";
        // note: VRL normalizes the VRL string!. use VRL.toString() to get actual string representation! 
        VRL vrl=new VRL(vrlstr); 
        vrlstr=vrl.toNormalizedString(); 
        
        // create STRING type: 
        VAttribute vrlStrAttr=new VAttribute(VAttributeType.STRING,"testvrl",vrlstr);
        // object value must match with String object. 
        testObjectValue(vrlStrAttr,vrlstr); // check String Value 
        
        // create VRL type: 
        VAttribute vrlAttr=new VAttribute("testvrl",vrl); 
        // object value must match with VRL object. 
        testObjectValue(vrlAttr,vrl); // check VRL Value 
    }
    
    @Test
    public void testCompare()
    {
        testStringAttrCompare("aap","noot"); 
        testStringAttrCompare("noot","aap");
        testStringAttrCompare("aap","aap"); 
        testStringAttrCompare("",""); 
        testStringAttrCompare("","aap"); 
        testStringAttrCompare("aap","");
        testStringAttrCompare(null,""); 
        testStringAttrCompare("",null); 
        testStringAttrCompare(null,null); 
        testStringAttrCompare(null,"aap"); 
        testStringAttrCompare("aap",null); 

    }
    
    void testStringAttrCompare(String val1,String val2)
    {
        int strComp=StringUtil.compare(val1,val2); 
        VAttribute a1=new VAttribute("name",val1); 
        VAttribute a2=new VAttribute("name",val2); 
        int attrComp=a1.compareTo(a2); 
        
        Assert.assertEquals("VAttribute compareTo() must result in same value a String compareTo()!",strComp,attrComp);
        
    }   
}
