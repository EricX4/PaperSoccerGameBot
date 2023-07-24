public class AnimationFunctions {
  private AnimationFunctions() {}
  public static double clamp(double x, double a, double b) {
    return Math.max(Math.min(x, b), a);
  }
  public static double lerp(double x, double a, double b) {
    return (1-x) * a + x * b;
  }
  public static double easeInOutQuintic(double x) {
    return x < 0.5 ? 16. * x * x * x * x * x : 1. - Math.pow(-2. * x + 2., 5) / 2.;
  }
  public static double easeInOutCubic(double x) {
    return x < 0.5 ? 4. * x * x * x : 1. - Math.pow(-2. * x + 2., 3) / 2.;
  }
  public static double easeInCubic(double x) {
    return x * x * x;
  }
  public static double easeInOutExpo(double x) {
    return x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;
  }
  public static double linear(double x) {
    return x;
  }
}