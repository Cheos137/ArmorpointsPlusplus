package dev.cheos.armorpointspp.core.adapter;

public interface IProfiler {
	void push(String s);
	void pop();
	void popPush(String s);
}
