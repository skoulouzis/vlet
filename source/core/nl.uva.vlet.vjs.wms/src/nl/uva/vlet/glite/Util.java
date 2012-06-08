package nl.uva.vlet.glite;

public class Util
{
    public static class Holder<T>
    {
        public T value;
        
        public Holder()
        {
            value=null; 
        }
        
        public T getValue()
        {
            return value;
        }
        
        public void setValue(T newValue)
        {
            value=newValue;
        }
        
        public boolean isNull()
        {
            return (value==null); 
        }
        
    }
    
    public static class BooleanHolder extends Holder<Boolean>
    {
        public BooleanHolder()
        {
            super();
        }
        
        public BooleanHolder(Boolean val)
        {
            this.value=val; 
        }
    }
    
    public static class IntegerHolder extends Holder<Integer>
    {
        public IntegerHolder()
        {
            super();
        }
        
        public IntegerHolder(Integer val)
        {
            this.value=val; 
        }
    }
 
    public static class LongHolder extends Holder<Long>
    {
        public LongHolder()
        {
            super();
        }
        
        public LongHolder(Long val)
        {
            this.value=val; 
        }
    }

    public static class StringHolder extends Holder<String>
    {
        public StringHolder()
        {
            super();
        }
        
        public StringHolder(String val)
        {
            this.value=val; 
        }
       
    }
    
 }
