/*
 * Copyright 2015 Matthew Timmermans
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nobigsoftware.dfalex;

class PrimeSizeFinder
{
	private final static int[] PRIME_SIZES = new int[] { 5, 7, 9, 11, 17, 23, 29, 37, 47, 59, 79, 101, 127, 163, 211, 269, 337, 421,
		529, 661, 827, 1039, 1301, 1627, 2039, 2549, 3187, 3989, 4987, 6241, 7817, 9781, 12227, 15287, 19121, 23909, 29917, 37397,
		46747, 58439, 73061, 91331, 114167, 142711, 178393, 222991, 278741, 348431, 435541, 544429, 680539, 850679,
		1063351, 1329197, 1661503, 2076881, 2596123, 3245171, 4056467, 5070599, 6338257, 7922821, 9903557, 12379453,
		15474317, 19342907, 24178639, 30223313, 37779149, 47223941, 59029963, 73787459, 92234327, 115292923, 144116201, 180145283,
		225181637, 281477047, 351846337, 439807933, 549759953, 687199949, 858999971, 1073749979, 1342187489, 1677734381 
	};
	
	public static int findPrimeSize(int minval)
	{
		//Linear search is fine here, since returning a size generally implies we're going
		//to do work proportional to that size anyway
		for (int i=0;i<PRIME_SIZES.length;++i)
		{
			if (PRIME_SIZES[i]>=minval)
			{
				return PRIME_SIZES[i];
			}
		}
		return Integer.MAX_VALUE; //Very handy that this is a Mersenne prime
	}
}
