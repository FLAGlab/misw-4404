package checker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CheckPassword {

	
	public String checkPasswordStrenght(char[] pass) {
		if(pass.length < 8)
			return "La contraseña es muy corta";
		
		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasNumber = false;
		boolean hasSymbol = false;
		char[] specialChars = "!@#$%^&*()_+-=,./<>?;:[]{}|".toCharArray();
		
		for(char s : pass) {
			if(Character.isUpperCase(s)) {
				hasUppercase = true;
			} else if(!Character.isUpperCase(s)) {
				hasLowercase = true;
			} else if(Character.isDigit(s)) {
				hasNumber = true;
			} else {
				for( int i=0; i<specialChars.length; i++) {
					if(specialChars[i] == s) {
						hasSymbol = true;
						break;
					}
				}
			}
			
			if(!hasUppercase) {
				return "La contraseña debe tener una mayúscula";
			} 
			if(!hasLowercase) {
				return "La contraseña debe tener una minúscula";
			}
			if(!hasNumber) {
				return "La contraseña debe tener un número";
			}
			if(!hasSymbol) {
				return "La contraseña debe tener un símbolo especial como !@#$%^&*()_+-=,./<>?;:[]{}|";
			}
		}
		return "la contraseña es segura";
	}
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Ingrese la contraseña: ");
			String password = br.readLine();
			char[] pass = password.toCharArray();
			CheckPassword checker =  new CheckPassword();
			String res = checker.checkPasswordStrenght(pass);
			System.out.println(res);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
