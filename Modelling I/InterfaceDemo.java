public class InterfaceDemo {

    static class Money {
        private final double amount;
        private final String currency;

        public Money(double amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        public String toString() {
            return amount + " " + currency;
        }
    }

    interface PaymentMethod {
        void pay(Money amount);
    }

    static class UPIPayment implements PaymentMethod {
        @Override
        public void pay(Money amount) {
            System.out.println("Paid using UPI: " + amount);
        }
    }

    static class CardPayment implements PaymentMethod {
        @Override
        public void pay(Money amount) {
            System.out.println("Paid using Card: " + amount);
        }
    }

    public static void main(String[] args) {
        PaymentMethod payment = new UPIPayment();
        payment.pay(new Money(5000, "INR"));

        payment = new CardPayment();
        payment.pay(new Money(5000, "INR"));
    }
}