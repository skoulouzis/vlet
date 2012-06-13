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
        doPresentationDateTimeString("0001-01-01 00:00:00.000");
        doPresentationDateTimeString("1970-01-13 01:23:45.678");  
        doPresentationDateTimeString("999999-12-31 23:59:59.999");  
    }
    
    public void doPresentationDateTimeString(String datestr)
    {
        Date date=Presentation.createDateFromNormalizedDateTimeString(datestr);
        String reversestr=Presentation.createNormalizedDateTimeString(date); 
        Assert.assertEquals("Normalized datetime strings should be the same",datestr,reversestr); 
    }
    
    @Test 
    public void testValueConstructors()
    {
        TestValues tValues=new TestValues(); 
        doTestValueConstructors(tValues); 
        
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
        doTestValueConstructors(tValues); 

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
        doTestValueConstructors(tValues); 

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
        
        doTestValueConstructors(tValues); 
        
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
        
        doTestValueConstructors(tValues); 
    }

  
    public void doTestValueConstructors(TestValues tValues)
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
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean1","true",new Boolean(true));
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean2","false",new Boolean(false));
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean3","True",new Boolean(true));
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean4","False",new Boolean(false));
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean5","TRUE",new Boolean(true));
        doTestStringValueConstructor(VAttributeType.BOOLEAN,"boolean6","FALSE",new Boolean(false));
        //
        doTestStringValueConstructor(VAttributeType.INT,"integer1","0", new Integer(0));
        doTestStringValueConstructor(VAttributeType.INT,"integer2","1", new Integer(1));
        doTestStringValueConstructor(VAttributeType.INT,"integer3","-1", new Integer(-1));
        doTestStringValueConstructor(VAttributeType.INT,"integer4",""+Integer.MAX_VALUE,new Integer(Integer.MAX_VALUE));  
        doTestStringValueConstructor(VAttributeType.INT,"integer5",""+Integer.MIN_VALUE,new Integer(Integer.MIN_VALUE));  
        doTestStringValueConstructor(VAttributeType.LONG,"long1","0",new Long(0));
        doTestStringValueConstructor(VAttributeType.LONG,"long2","1",new Long(1));
        doTestStringValueConstructor(VAttributeType.LONG,"long3","-1",new Long(-1));
        doTestStringValueConstructor(VAttributeType.LONG,"long4",""+Long.MAX_VALUE,new Long(Long.MAX_VALUE));  
        doTestStringValueConstructor(VAttributeType.LONG,"long5",""+Long.MIN_VALUE,new Long(Long.MIN_VALUE));  
        // watch out for rounding errors from decimal to IEEE floats/doubles !
        doTestStringValueConstructor(VAttributeType.FLOAT,"float1","0.0",new Float(0.0));
        doTestStringValueConstructor(VAttributeType.FLOAT,"float2","1.0",new Float(1.0));
        doTestStringValueConstructor(VAttributeType.FLOAT,"float3","-1.0",new Float(-1.0));
        doTestStringValueConstructor(VAttributeType.FLOAT,"float4",""+Float.MAX_VALUE,new Float(Float.MAX_VALUE));
        doTestStringValueConstructor(VAttributeType.FLOAT,"float5",""+Float.MIN_VALUE,new Float(Float.MIN_VALUE));
        // todo: check rounding errors
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double1","0.0",new Double(0.0));
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double2","1.0",new Double(1.0));
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double3","-1.0",new Double(-1.0));
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double4","-1.123456",new Double(-1.123456));
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double5",""+Double.MAX_VALUE,new Double(Double.MAX_VALUE));
        doTestStringValueConstructor(VAttributeType.DOUBLE,"double6",""+Double.MIN_VALUE,new Double(Double.MIN_VALUE));
        // STRING
        doTestStringValueConstructor(VAttributeType.STRING,"string1","value","value");
        doTestStringValueConstructor(VAttributeType.STRING,"string2","","");
        // allow NULL 
        doTestStringValueConstructor(VAttributeType.STRING,"string3",null,null);
        
        // DATETIME
        long millies=System.currentTimeMillis(); 
        Date dateVal=Presentation.createDate(millies); 
        doTestStringValueConstructor(VAttributeType.TIME,"name",Presentation.createNormalizedDateTimeString(millies),dateVal);  
        // VRL 
        String vrlStr="file://user@host.domain:1234/Directory/A File/";
        VRL vrl=new VRL(vrlStr);
        doTestStringValueConstructor(VAttributeType.VRL,"name","file://user@host.domain:1234/Directory/A File/",vrl);
               
    }
    
    public void doTestStringValueConstructor(VAttributeType type,String name,String value,Object objectValue) 
    {
        // basic constructor tests 
        VAttribute attr=VAttribute.createFromString(type,name,value);
        // check type,name and String value 
        Assert.assertEquals("Type must be:"+type,type,attr.getType());
        Assert.assertEquals("String values should match (if parsed correctly) for type:"+type,value,attr.getStringValue()); 
        Assert.assertEquals("Attribute name must match",name,attr.getName()); 
        Assert.assertTrue("isType() must return true for attr:"+attr,attr.isType(type)); 
        
        checkObjectValueType(attr,objectValue); 
    }

    // test whether object has matching type and native value 
    void checkObjectValueType(VAttribute attr,Object objValue)
    {
        VAttributeType type=getObjectVAttributeType(objValue);
        VAttributeType type2=VAttributeType.getObjectType(objValue,null); 
        Assert.assertEquals("VAttributeType.getObjectType() and unit test getObjectType() must agree.",type,type2);
        if (objValue==null)
        {
            Assert.assertNull("NULL object msut have NULL type.",objValue);
            return; // NULL value.  
        }
        
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
            case STRING:
                Assert.assertTrue("getStringValue() must match native type!",(attr.getStringValue()==((String)objValue))); 
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
            default:
                Assert.fail("Can not check type:"+type); 

        }
        
        //compareDateValues(attr.getDateValue(),(Date)objValue))); 
    }

    private boolean compareDateValues(Date val1,Date val2)
    {
        boolean result=false; 

        if (val1==val2)
            result=true; 
        
        if (result==false)
        {
            if ( val1.toString().equals(val2.toString()) ) 
                result=true; 
        }
       
        String unistr1=Presentation.createNormalizedDateTimeString(val1); 
        String unistr2=Presentation.createNormalizedDateTimeString(val2); 

        // also Compare Presentation string implementations. 
        if (result)
        {
            Assert.assertEquals("Normalize date/time strings should be equal",unistr1,unistr2); 
        }
        else
        {
            Assert.assertEquals("Normalize date/time strings should NOT be equal",unistr1,unistr2); 
        }
        
        return result; 
    }

    // Unit test implementation: Different then VAttributeType to assert similar implementations
    VAttributeType getObjectVAttributeType(Object obj)
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
        checkObjectValueType(vrlStrAttr,vrlstr); // check String Value 
        
        // create VRL type: 
        VAttribute vrlAttr=new VAttribute("testvrl",vrl); 
        // object value must match with VRL object. 
        checkObjectValueType(vrlAttr,vrl); // check VRL Value 
    }
    
    @Test
    public void testVAttributeStringCompare()
    {
        doVAttributeStringCompare("aap","noot"); 
        doVAttributeStringCompare("noot","aap");
        doVAttributeStringCompare("aap","aap"); 
        doVAttributeStringCompare("",""); 
        doVAttributeStringCompare("","aap"); 
        doVAttributeStringCompare("aap","");
        doVAttributeStringCompare(null,""); 
        doVAttributeStringCompare("",null); 
        doVAttributeStringCompare(null,null); 
        doVAttributeStringCompare(null,"aap"); 
        doVAttributeStringCompare("aap",null); 
    }
    
    void doVAttributeStringCompare(String val1,String val2)
    {
        int strComp=StringUtil.compare(val1,val2); 
        VAttribute a1=new VAttribute("name",val1); 
        VAttribute a2=new VAttribute("name",val2); 
        int attrComp=a1.compareTo(a2); 
        
        Assert.assertEquals("VAttribute compareTo() must result in same value a String compareTo()!",strComp,attrComp);
    }   
    
    @Test
    public void testVAttributeIntCompare()
    {
        doVAttributeIntLongCompare(0,0); 
        doVAttributeIntLongCompare(-1,0); 
        doVAttributeIntLongCompare(0,-1); 
        doVAttributeIntLongCompare(-1,-1);
        
        doVAttributeIntLongCompare(0,0); 
        doVAttributeIntLongCompare(1,0); 
        doVAttributeIntLongCompare(0,1); 
        doVAttributeIntLongCompare(1,1);
        
        doVAttributeIntLongCompare(-1,1);
        doVAttributeIntLongCompare(1,-1);
    }
    
    void doVAttributeIntLongCompare(int val1,int val2)
    {
        // int inComp=Integer.compare(val1, val2);  // java 1.7 
        int intComp=new Integer(val1).compareTo(val2);
        VAttribute a1=new VAttribute("name1",val1); 
        VAttribute a2=new VAttribute("name2",val2); 
        int attrComp=a1.compareTo(a2); 
        Assert.assertEquals("VAttribute (int)compareTo() must result in same value a Integer compareTo()!",intComp,attrComp);
       
        long lval1=val1,lval2=val2; 
        // int longComp=Long.compare(lval1, lval2); // java 1.7  
        int longComp=new Long(lval1).compareTo(lval2); 
        
        a1=new VAttribute("name1",lval1); 
        a2=new VAttribute("name2",lval2); 
        attrComp=a1.compareTo(a2); 
        Assert.assertEquals("VAttribute (long)compareTo() must result in same value a Long compareTo()!",longComp,attrComp);
        
        // paranoia: 
        Assert.assertEquals("Integet.compare() and Long.compare() do not match!",intComp,longComp); 
    }   
}
