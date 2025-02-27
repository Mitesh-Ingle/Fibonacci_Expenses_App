package application;

public class FibonacciGenerator {

	public static String generateFibonacci(int n) {
		if (n <= 0)
			return "Invalid Input";

		StringBuilder result = new StringBuilder();
		int a = 0, b = 1;
		result.append(a).append(" ").append(b).append(" ");

		for (int i = 2; i < n; i++) {
			int next = a + b;
			result.append(next).append(" ");
			a = b;
			b = next;
		}
		return result.toString();
	}

}
