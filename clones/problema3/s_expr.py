def parse_expression(expr):
    tokens = tokenize(expr)
    return read_from_tokens(tokens)

def tokenize(expr):
    return expr.replace('(', ' ( ').replace(')', ' ) ').split()

def atomize(token):
    try:
        return float(token)
    except ValueError:
        return token

def read_from_tokens(tokens):
    if len(tokens) == 0:
        raise SyntaxError("Unexpected end of expression")

    token = tokens.pop(0)

    if token == '(':
        nested_expr = []
        while len(tokens) > 0 and tokens[0] != ')':
            nested_expr.append(read_from_tokens(tokens))
        if len(tokens) == 0:
            raise SyntaxError("Unexpected end of expression")
        elif len(nested_expr) < 3:
            raise SyntaxError("Invalid expression")
        else:
            tokens.pop(0)
        return nested_expr
    elif token == ')':
        raise SyntaxError("Unexpected closing parenthesis")
    else:
        try: 
            return float(token)
        except ValueError:
            return token

# Operators
def add(*args):
    return sum(args)
def subtract(*args):
    return args[0] - sum(args[1:])
def multiply(*args):
    result = 1
    for arg in args:
        result *= arg
    return result
def divide(*args):
    result = args[0]
    for arg in args[1:]:
        result /= arg
    return result

OPERATORS = {
    "+": lambda *args: sum(args),
    "-": subtract,
    "*": multiply,
    "/": divide
}

def evaluate(expr):
    if isinstance(expr, list):
        operator = expr[0]
        if operator not in OPERATORS:
            raise SyntaxError("Unknown operator: " + operator)
        operands = expr[1:]
        operands = [evaluate(operand) for operand in operands]
        return OPERATORS[operator](*operands)
        
    else:
        return expr

if __name__ == "__main__":
    # Ejemplo expresiones validas:
    # "(+ 10 10 10)"
    # "(+ 10 (* 5 2) (- 8 3) (/ 20 4))"

    while True:
        expr = input(">>> ")
        if expr == "exit" or expr == "quit" or expr == "":
            break
        try:
            result = parse_expression(expr)
            print(evaluate(result))
        except SyntaxError as e:
            print("Syntax error: ", e)
