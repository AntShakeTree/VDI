package com.opzoon.vdi.core.util;

public class Pair<L, R> {

	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		return (this.left.hashCode() + " " + this.right.hashCode()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Pair)) {
			return false;
		}
		Pair<?, ?> that = (Pair<?, ?>) obj;
		return this.left.equals(that.left)
				&& this.right.equals(that.right);
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

}
