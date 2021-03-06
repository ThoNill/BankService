package data;

public class IBAN {
        private String iban;

        public IBAN(String iban) {
            super();
            this.iban = iban;
        }

        @Override
        public String toString() {
            return iban;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((iban == null) ? 0 : iban.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IBAN other = (IBAN) obj;
            if (iban == null) {
                if (other.iban != null)
                    return false;
            } else if (!iban.equals(other.iban))
                return false;
            return true;
        }
        
}
