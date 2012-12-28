/* BwtSort.h -- BWT block sorting
2008-03-26
Igor Pavlov
Public domain */

#ifndef __BWTSORT_H
#define __BWTSORT_H

#include "Types.h"

/* use BLOCK_SORT_EXTERNAL_FLAGS if blockSize can be > 1M */
/* #define BLOCK_SORT_EXTERNAL_FLAGS */

#ifdef BLOCK_SORT_EXTERNAL_FLAGS
#define BLOCK_SORT_EXTERNAL_SIZE(blockSize) ((((blockSize) + 31) >> 5))
#else
#define BLOCK_SORT_EXTERNAL_SIZE(blockSize) 0
#endif

#define BLOCK_SORT_BUF_SIZE(blockSize) ((blockSize) * 2 + BLOCK_SORT_EXTERNAL_SIZE(blockSize) + (1 << 16))

UInt32 BlockSort(UInt32 *indices, const Byte *data, UInt32 blockSize);

#endif
