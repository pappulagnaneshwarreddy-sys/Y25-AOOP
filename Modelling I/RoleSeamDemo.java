public class RoleSeamDemo {

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
            System.out.println("UPI payment: " + amount);
        }
    }

    static class CardPayment implements PaymentMethod {
        @Override
        public void pay(Money amount) {
            System.out.println("Card payment: " + amount);
        }
    }

    static class BookingService {
        private final PaymentMethod paymentMethod;

        public BookingService(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public void confirmBooking(Money amount) {
            paymentMethod.pay(amount);
            System.out.println("Booking confirmed");
        }
    }

    public static void main(String[] args) {
        PaymentMethod payment = new UPIPayment();
        BookingService service = new BookingService(payment);
        service.confirmBooking(new Money(10000, "INR"));
    }
}