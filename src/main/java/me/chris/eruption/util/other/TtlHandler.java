package me.chris.eruption.util.other;

public interface TtlHandler<E> {

	void onExpire(E element);

	long getTimestamp(E element);

}
