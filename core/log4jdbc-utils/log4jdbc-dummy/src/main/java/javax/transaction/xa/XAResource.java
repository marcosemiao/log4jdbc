package javax.transaction.xa;

public interface XAResource {
	public final static int TMENDRSCAN = 8388608;

	public final static int TMFAIL = 536870912;

	public final static int TMJOIN = 2097152;

	public final static int TMNOFLAGS = 0;

	public final static int TMONEPHASE = 1073741824;

	public final static int TMRESUME = 134217728;

	public final static int TMSTARTRSCAN = 16777216;

	public final static int TMSUCCESS = 67108864;

	public final static int TMSUSPEND = 33554432;
}
