package dev.cheos.armorpointspp.core.adapter;

public interface IMath {
	int ceil(float f);
	int floor(float f);
	int clamp(int i, int min, int max);
	long floor(double d);
}
