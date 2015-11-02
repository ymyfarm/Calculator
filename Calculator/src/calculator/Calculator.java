package calculator;

/* Calculator.java
 * 
 * Candidate: Sammy Yao
 * 
 * This is a calculator program that evaluates expressions in a very simple
 * integer expression language. The program takes an input on the command line,
 * computes the result, and prints it to the console.
 * 
 * 
 * Examples:
 * 
 * java Calculator "add(1, 2)" ==> 3
 * java Calculator "add(1, mult(2, 3))" ==> 7
 * java Calculator "mult(add(2, 2), div(9, 3))" ==> 12
 * java Calculator "let(a, 5, add(a, a))" ==> 10
 * java Calculator "let(a, 5, let(b, mult(a, 10), add(b, a)))" ==> 55
 * java Calculator "let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))" ==> 40
 * 
 * 
 * This program is written based on the following assumptions:
 * 
 * 1) If a variable is declared in a let statement but is not used, the input is treated as valid. 
 * 2) If there is extra whitespace in the input, the input is treated as valid.
 */

import java.util.HashMap;
import java.util.logging.*;

public class Calculator {
	
	/* Logging constants */
	static final Level DEBUG = Level.FINE;
	static final Level INFO = Level.INFO;
	static final Level ERROR = Level.SEVERE;
    
    /* HashMap to store variables and their values */
    private HashMap<String, Integer> vars;
    
    /* Create logger to log DEBUG, INFO, and ERROR messages */
    private Logger logger;
    
    /* Constructor */
    public Calculator() {
        vars = new HashMap<String, Integer>();
        logger = Logger.getLogger(Calculator.class.getName());
    }
    
    /* Function to evaluate an expression recursively. */
    public int evaluateExpression(String expr) {
    	String expr1 = "", expr2 = "", var = "";
    	int value = 0, result = 0;
    	
    	logger.log(DEBUG, "Evaluating expression: " + expr);
    	
        try {
        	if (expr.matches("\\d+")) {
        		/* The expression is a number */
        		result = Integer.parseInt(expr);
        	} else if (expr.startsWith("add(")) {
        		/* Addition */
            	if (!expr.endsWith(")")) {
            		throw new IllegalArgumentException("Expression " + expr + " does not end with )!");
            	}
            	expr1 = extractOperand(expr.substring(4));
            	expr2 = expr.substring(expr1.length() + 5, expr.length() - 1);
            	logger.log(DEBUG, "First operand: " + expr1);
            	logger.log(DEBUG, "Second operand: " + expr2);
            	result = evaluateExpression(expr1) + evaluateExpression(expr2);
        	} else if (expr.startsWith("sub(")) {
        		/* Subtraction */
            	if (!expr.endsWith(")")) {
            		throw new IllegalArgumentException("Expression " + expr + " does not end with )!");
            	}
            	expr1 = extractOperand(expr.substring(4));
            	expr2 = expr.substring(expr1.length() + 5, expr.length() - 1);
            	logger.log(DEBUG, "First operand: " + expr1);
            	logger.log(DEBUG, "Second operand: " + expr2);
            	result = evaluateExpression(expr1) - evaluateExpression(expr2);
        	} else if (expr.startsWith("mult(")) {
        		/* Multiplication */
            	if (!expr.endsWith(")")) {
            		throw new IllegalArgumentException("Expression " + expr + " does not end with )!");
            	}
            	expr1 = extractOperand(expr.substring(5));
            	expr2 = expr.substring(expr1.length() + 6, expr.length() - 1);
            	logger.log(DEBUG, "First operand: " + expr1);
            	logger.log(DEBUG, "Second operand: " + expr2);
            	result = evaluateExpression(expr1) * evaluateExpression(expr2);
        	} else if (expr.startsWith("div(")) {
        		/* Division */
            	if (!expr.endsWith(")")) {
            		throw new IllegalArgumentException("Expression " + expr + " does not end with )!");
            	}
            	expr1 = extractOperand(expr.substring(4));
            	expr2 = expr.substring(expr1.length() + 5, expr.length() - 1);
            	logger.log(DEBUG, "First operand: " + expr1);
            	logger.log(DEBUG, "Second operand: " + expr2);
                int divisor = evaluateExpression(expr2);
                if (divisor == 0) {
                    throw new ArithmeticException("Division by 0 error!");
                }
                result = evaluateExpression(expr1) / divisor;
            } else if (expr.startsWith("let(")) {
            	/* Let operation */
            	if (!expr.endsWith(")")) {
            		throw new IllegalArgumentException("Expression " + expr + " does not end with )!");
            	}
            	var = extractOperand(expr.substring(4));
            	if (vars.containsKey(var)) {
            		/* Variable already declared */
            		throw new IllegalArgumentException("Variable \"" + var + "\" already declared!");
            	}
            	expr1 = extractOperand(expr.substring(var.length() + 5));
            	value = evaluateExpression(expr1);
            	vars.put(var, value);
            	result = evaluateExpression(expr.substring(var.length() + expr1.length() + 6, expr.length() - 1));
            	logger.log(DEBUG, "Added variable " + var + " with value " + value);
            	logger.log(DEBUG, "First operand: " + expr1);
            	logger.log(DEBUG, "Second operand: " + result);
            } else if (expr.matches("[a-zA-Z]+")) {
            	/* Expression is alphabetic. Check if it's a valid variable */
                if (!vars.containsKey(expr)) {
                    /* Undeclared variable */
                    throw new IllegalArgumentException("Undeclared variable \"" + expr + "\"!");
                }
                return vars.get(expr);
            } else {
            	throw new IllegalArgumentException("Invalid expression \"" + expr + "\"! Must be one of add/sub/mult/div/let or a number or variable!");
            }
            
        	/* Remove the variable now that we're done with it and it can be reused in subsequent let statements. */
        	vars.remove(var);
        	logger.log(DEBUG, "Removed variable " + var);
        } catch (Exception e) {
            logger.log(ERROR, e.toString());
            System.exit(0);
        }
        
        logger.log(DEBUG, "Returned result is " + result);
        return result;
    }
    
    /* Helper function to extract an operand */
    public String extractOperand(String expr) {
        int i = 0, level = 0;
        boolean found = false;
        
        try {
            /* Counter to keep track of the current level of brackets. Initialize to
             * 0 to indicate that we haven't read the first opening bracket yet.
             */
            for (i = 0; i < expr.length(); i++) {
                if (expr.charAt(i) == '(') {
                    /* Start of the first operand. */
                    level++;
                } else if (expr.charAt(i) == ')') {
                    /* End of a sub-expression. */
                    level--;
                    if (level < 0) {
                        /* Invalid expression */
                     throw new IllegalArgumentException("Invalid string! Extra closing bracket!");
                    }
                } else if ((expr.charAt(i) == ',') && (level == 0)) {
                    /* Found a comma separator for the operand */
                    found = true;
                    break;
                }
            }

            if (!found) {
                /* We've walked all the way to the end, but were not able to find a legal comma separator for the
                 * first operand. Therefore, we have an invalid expression.
                 */
             throw new IllegalArgumentException("Invalid expression. No legal comma separator for first operand!");
            } 
        } catch (Exception e) {
            logger.log(ERROR, e.toString());
        }

        return (expr.substring(0, i));
    }
    
    /* Function to do an initial check on the expression to see if the brackets are matching. */
    public boolean checkBrackets(String expr) {
    	boolean result = true;
    	int count = 0;
    	
    	for (int i = 0; i < expr.length(); i++) {
    		if (expr.charAt(i) == '(') {
    			count++;
    		} else if (expr.charAt(i) == ')') {
    			count--;
    		}
    		if (count < 0) {
    			/* At no point in the expression can there be more closing brackets than opening ones. */
    			result = false;
    			return result;
    		}
    	}
    	
    	if (count != 0) {
    		result = false;
    	}
    	
    	return result;
    }
    
    public static void main(String[] args) {
        int result = 0;
        
        Calculator myCalc = new Calculator();
        
        try {
            if (args.length != 1) {
                throw new IllegalArgumentException("There must be exactly one argument! e.g. add(1, 2)");
            }
            
            if (!myCalc.checkBrackets(args[0])) {
            	throw new IllegalArgumentException("Brackets of the input expression \"" + args[0] + "\" do NOT match!");
            }
            
            /* Normalize the expression by removing all the whitespaces around
             * opening/closing brackets and the comma separator.
             * Any remaining whitespaces after the normalization will indicate
             * an invalid expression during evaluation later on.
             */

            /* Remove all white spaces before and after the opening and closing brackets
             */
            String expr_v1 = args[0].replaceAll("(.)(\\s+)([()])(\\s+)(.)", "$1$3$5");

            /* Remove all spaces before and after the separator ,
             */
             String expr_v2 = expr_v1.replaceAll("(.)(\\s*)([,])(\\s*)(.)", "$1$3$5");

             String expr_v3 = expr_v2.replaceAll("(.)([,])(\\s+)(.)", "$1$2$4");
             
             myCalc.logger.setLevel(DEBUG);

             result = myCalc.evaluateExpression(expr_v3);
        
            /* Print the result only if the expression is valid. Otherwise, an error
             * message has already been logged.
             */
            System.out.println(result);
        } catch (Exception e) {
            myCalc.logger.log(ERROR, e.toString());
            return;
        }
    }
}