/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: Expressions.java,v 1.3 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */ 
// source: 

package nl.uva.vlet.expressions;

import java.util.regex.Pattern;


/**
 * Collection Class of Expressions
 * 
 * @author P.T. de Boer
 *
 */

public class Expressions
{
	public static class ExpressionError extends Error 
	{
		private static final long serialVersionUID = 3969350873791447622L;

		public ExpressionError(String msg)
		{
			super(msg); 
		}
	}; 
	
	public static enum Operator 
	{
		/*ASSIGN(2),*/ EQUALS(2),AND(2),OR(2),NOT(1),ISIN(2),ISNOTIN(2),MATCHES(2);

		int nrOfExpressions=0;  

		Operator(int nrOfExpress)
		{
			this.nrOfExpressions=nrOfExpress; 
		}
	}
	
	public static abstract class Expression
	{
		public abstract Parameter evaluate(Stack stack);  
	}
	
	/** Parameter expression van be used as Variable or Argument */ 
	public static class ParameterExpression extends Expression
	{
		Parameter par;

		public ParameterExpression(String name)
		{
			new Parameter(name,(String)null); 
		}
		
		/** Parameter Expression evaluates Variable name from  from stack */ 
		@Override
		public Parameter evaluate(Stack stack)
		{
			// Get Parameter off stack :
			Parameter value=stack.getParameter(par.getName());
			return value; 
		} 
		
	}
	
	/**
	 * Value Expression uses namesless parameter object
	 * to store it's value.  
	 */ 
	public static class ValueExpression extends Expression
	{
		Parameter par;

		public ValueExpression(String value)
		{
			new Parameter((String)null,value); 
		}
		
		public ValueExpression(int value)
		{
			new Parameter((String)null,value); 
		}
		
		public ValueExpression(long value)
		{
			new Parameter((String)null,value); 
		}
		
		public ValueExpression(double value)
		{
			new Parameter((String)null,value); 
		}
		
		/** Returns Value of this expression (= Parameter object itself) */  
		@Override
		public Parameter evaluate(Stack stack)
		{
			return par; 
		} 
		
	}
	
	/** Mono operator expression */ 
	public static class MonOpExpression extends Expression
	{
		Operator operator; 
		Expression expression; 
	
		public MonOpExpression(Operator op,Expression expr) 
		{
			if (operator.nrOfExpressions!=1)
				throw new ExpressionError("Mono Operator Expression needs mono operator:"+op); 

			this.operator=op;
			this.expression=expr;  
		}
		
		@Override
		public Parameter evaluate(Stack stack)
		{
			Parameter value=expression.evaluate(stack);
			
			switch(operator)
			{
				case NOT:  
					return new Parameter(Parameter.RESULT, (value.getBooleanValue() !=true)); 
				default:
					throw new ExpressionError("Illegal Operator type:"+operator);
			}
		}
		
	}

	
	/** Matches a two expression type (hence binary!) operator pattern */ 
	public static class BinOpExpression extends Expression
	{
		Operator operator; 
		Expression leftExpression;
		Expression rightExpression;

		public BinOpExpression(Operator op,Expression left,Expression right) 
		{
			if (operator.nrOfExpressions!=2)
				throw new ExpressionError("Binary Operator Expression needs binary operator:"+op); 

			this.operator=op; 
			this.leftExpression=left;
			this.rightExpression=right; 
		}
		

		@Override
		public Parameter evaluate(Stack stack)
		{
			Parameter leftValue=leftExpression.evaluate(stack);
			Parameter rightValue=rightExpression.evaluate(stack);
			Parameter result=null; 
			
			switch(operator)
			{
				case AND: 
					result=new Parameter(Parameter.RESULT, (leftValue.getBooleanValue() && rightValue.getBooleanValue()) );
					break; 
				case OR:  
					result=new Parameter(Parameter.RESULT, (leftValue.getBooleanValue() || rightValue.getBooleanValue()) );
					break; 
				case EQUALS: 
					result=new Parameter(Parameter.RESULT, (leftValue.compareTo(rightValue))); 
					break; 
				case ISIN: 
					result=new Parameter(Parameter.RESULT,rightValue.hasSetValue(leftValue.getStringValue()));
					break;
				case MATCHES: 
					// Warning: arguments are switched 
					boolean bool=matchesRegExp(rightValue.getStringValue(),leftValue.getStringValue()); 
					result=new Parameter(Parameter.RESULT,bool); 
				default:
					throw new ExpressionError("Illegal Operator type:"+operator);
			}
			
			return result; 
		}
		
		public static class ForAllExpression extends Expression
		{
			Expression expressions[]=null; 
			
			public ForAllExpression(Expression exprs[])
			{
				expressions=exprs; 
			}
			
			@Override
			public Parameter evaluate(Stack stack)
			{
				Parameter result=null; 
				
				for (Expression expr:expressions)
				{
					result = expr.evaluate(stack);
					
					if (result.getBooleanValue()==false)
						return result; 
				}
				// not one expression is false, so last expression must be true: 
				return result; 
			}
		}
		
		public static class ForAnyExpression extends Expression
		{
			Expression expressions[]=null; 
			
			public ForAnyExpression(Expression exprs[])
			{
				expressions=exprs; 
			}
			
			@Override
			public Parameter evaluate(Stack stack)
			{
				Parameter result=null; 
				
				for (Expression expr:expressions)
				{
					result = expr.evaluate(stack);
					
					if (result.getBooleanValue()==true)
						return result; 
				}
				// not any expression is true, so last result==false: 
				return result; 
			}
		}
	}

	public static boolean matchesRegExp(String regExp,CharSequence sequence) 
	{
		return Pattern.matches(regExp,sequence); 
	}
}
