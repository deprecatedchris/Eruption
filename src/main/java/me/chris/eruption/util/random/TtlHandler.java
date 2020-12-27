package me.chris.eruption.util.random;

public interface TtlHandler<E> {

	void onExpire(E element);

	long getTimestamp(E element);

}
