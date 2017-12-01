package javax.transaction.xa;

public interface Xid {

	public final static int MAXGTRIDSIZE = 64;

	public final static int MAXBQUALSIZE = 64;

	public int getFormatId();

	public byte[] getGlobalTransactionId();

	public byte[] getBranchQualifier();
}
