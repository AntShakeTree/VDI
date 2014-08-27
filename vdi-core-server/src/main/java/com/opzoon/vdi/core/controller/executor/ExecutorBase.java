package com.opzoon.vdi.core.controller.executor;

import com.opzoon.vdi.core.controller.TaskInfo;

public abstract class ExecutorBase
{

	public abstract ExecuteResult execute(TaskInfo task);

	public static class ExecuteResult
	{
		private int errorCode = 0; // 0: no error
		private String errorString;

		/**
		 * @return the errorCode
		 */
		public int getErrorCode()
		{
			return errorCode;
		}

		/**
		 * @param errorCode
		 *            the errorCode to set
		 */
		public void setErrorCode(int errorCode)
		{
			this.errorCode = errorCode;
		}

		/**
		 * @return the errorString
		 */
		public String getErrorString()
		{
			return errorString;
		}

		/**
		 * @param errorString
		 *            the errorString to set
		 */
		public void setErrorString(String errorString)
		{
			this.errorString = errorString;
		}

	}
}
