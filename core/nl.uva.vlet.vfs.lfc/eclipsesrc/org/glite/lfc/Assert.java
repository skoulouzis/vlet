
package org.glite.lfc;

public final class Assert
{
    private Assert()
    {
    }

    public static boolean isLegal(boolean expression)
    {
        return isLegal(expression, "");
    }

    public static boolean isLegal(boolean expression, String message)
    {
        if(!expression)
            throw new IllegalArgumentException(message);
        else
            return expression;
    }

    public static void isNotNull(Object object)
    {
        isNotNull(object, "");
    }

    public static void isNotNull(Object object, String message)
    {
        if(object == null)
            throw new AssertionFailedException("null argument:" + message);
        else
            return;
    }

    public static boolean isTrue(boolean expression)
    {
        return isTrue(expression, "");
    }

    public static boolean isTrue(boolean expression, String message)
    {
        if(!expression)
            throw new AssertionFailedException("assertion failed: " + message);
        else
            return expression;
    }
}

