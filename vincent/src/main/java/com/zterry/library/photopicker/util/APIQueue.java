package com.zterry.library.photopicker.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class APIQueue {
	private final int HIGH_POOL_SIZE = 5;
	private final int NORMAL_POOL_SIZE = 3;
	private final int LOW_POOL_SIZE = 2;
	
	public static final int HIGH_LEVEL = 0;
	public static final int NORMAL_LEVEL = 1;
	public static final int LOW_LEVEL = 3;
	
	private ThreadPoolExecutor mHighPool;
	private ThreadPoolExecutor mNormalPool;
	private ThreadPoolExecutor mLowPool;
	
	private static APIQueue mInstance;
	
	private APIQueue() {
		mHighPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(HIGH_POOL_SIZE);
		mNormalPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NORMAL_POOL_SIZE);
		mLowPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(LOW_POOL_SIZE);
	}
	
	public static APIQueue getInstance() {
		if (mInstance == null) {
			mInstance = new APIQueue();
		}
		return mInstance;
	}

	/**
	 * NORMAL_LEVEL
	 * @param runnable
	 */
	public void execute(Runnable runnable) {
		execute(runnable, NORMAL_LEVEL);
	}
	
	/**
	 * HIGH_LEVEL
	 * @param runnable
	 */
	public void executeHigh(Runnable runnable) {
		execute(runnable, HIGH_LEVEL);
	}
	
	/**
	 * LOW_LEVEL
	 * @param runnable
	 */
	public void executeLow(Runnable runnable) {
		execute(runnable, LOW_LEVEL);
	}
	
	public void execute(Runnable runnable, int priority) {
		if (runnable != null) {
			switch (priority) {
				case HIGH_LEVEL:
					if (mHighPool.getActiveCount() == HIGH_POOL_SIZE && mLowPool.getActiveCount() < LOW_POOL_SIZE) {
						mLowPool.execute(runnable);
					}
					else if (mHighPool.getActiveCount() == HIGH_POOL_SIZE && mNormalPool.getActiveCount() < NORMAL_POOL_SIZE) {
						mNormalPool.execute(runnable);
					}
					else {
						mHighPool.execute(runnable);
					}
					break;
				case NORMAL_LEVEL:
						mNormalPool.execute(runnable);
					break;
				case LOW_LEVEL:
					mLowPool.execute(runnable);
					break;
			}
		}
	}
}
