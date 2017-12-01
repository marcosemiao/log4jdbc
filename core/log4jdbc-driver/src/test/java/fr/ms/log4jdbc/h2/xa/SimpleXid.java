package fr.ms.log4jdbc.h2.xa;

import java.util.Arrays;

import javax.transaction.xa.Xid;

public class SimpleXid implements Xid {

	private final int formatId;
	private final byte[] branchQualifier;
	private final byte[] globalTransactionId;

	public SimpleXid(final int formatId, final byte[] branchQualifier, final byte[] globalTransactionId) {
		this.formatId = formatId;
		this.branchQualifier = branchQualifier;
		this.globalTransactionId = globalTransactionId;
	}

	public byte[] getBranchQualifier() {
		return branchQualifier;
	}

	public int getFormatId() {
		return formatId;
	}

	public byte[] getGlobalTransactionId() {
		return globalTransactionId;
	}

	
	public int hashCode() {
		return formatId;
	}

	
	public boolean equals(final Object other) {
		if (other instanceof Xid) {
			final Xid xid = (Xid) other;
			if (xid.getFormatId() == formatId) {
				if (Arrays.equals(branchQualifier, xid.getBranchQualifier())) {
					if (Arrays.equals(globalTransactionId, xid.getGlobalTransactionId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	public String toString() {
		return "xid:" + formatId;
	}
}
