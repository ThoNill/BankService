package data;

public class BIC {
     private String bic;
    
    public BIC(String bic) {
        super();
        this.bic = bic;
    }

    @Override
    public String toString() {
        return  bic;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bic == null) ? 0 : bic.hashCode());
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
        BIC other = (BIC) obj;
        if (bic == null) {
            if (other.bic != null)
                return false;
        } else if (!bic.equals(other.bic))
            return false;
        return true;
    }
    
}
