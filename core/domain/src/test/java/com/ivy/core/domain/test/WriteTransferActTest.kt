package com.ivy.core.domain.test

import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.WriteTrnsBatchAct
import com.ivy.core.domain.action.transaction.account
import com.ivy.core.domain.action.transaction.transfer.ModifyTransfer
import com.ivy.core.domain.action.transaction.transfer.TransferByBatchIdAct
import com.ivy.core.domain.action.transaction.transfer.TransferData
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WriteTransferActTest {
    //step1: Add dependencies needed
    private lateinit var writeTransferAct: WriteTransferAct
    private lateinit var writeTrnsAct: WriteTrnsAct
    private lateinit var writeTrnsBatchAct: WriteTrnsBatchAct
    private lateinit var transferByBatchIdAct: TransferByBatchIdAct


    @BeforeEach
    fun setUp() {
        //step2: Setup and pass mock value
        writeTrnsAct = mockk(relaxed = true)
        writeTrnsBatchAct = mockk(relaxed = true)
        transferByBatchIdAct = mockk(relaxed = true)
        writeTransferAct = WriteTransferAct(
            writeTrnsAct = writeTrnsAct,
            writeTrnsBatchAct = writeTrnsBatchAct,
            transferByBatchIdAct = transferByBatchIdAct
        )
    }

    @Test
    fun `Add transfer, fees are considered`() = runBlocking {
        //step3: Write Fake data
        writeTransferAct(
            ModifyTransfer.add(
                data = TransferData(
                    amountFrom = Value(amount = 50.0, currency = "EUR"),
                    amountTo = Value(amount = 60.0,  currency = "USD"),
                    accountFrom = account().copy(
                        name = "Test account1"
                    ),
                    accountTo = account().copy(
                        name = "Test account2"
                    ),
                    category = null,
                    time = TrnTime.Actual(LocalDateTime.now()),
                    title = "Test transfer",
                    description = "Test transfer description",
                    fee = Value(amount = 5.0, currency = "EUR"),
                    sync = Sync(
                        state = SyncState.Syncing,
                        lastUpdated = LocalDateTime.now()
                    )
                )
            )
        )
        //step4: Write assertion and run test
        coVerify {
            writeTrnsBatchAct(
                match {
                    it as WriteTrnsBatchAct.ModifyBatch.Save

                    val from = it.batch.trns[0]
                    val to = it.batch.trns[1]
                    val fee = it.batch.trns[2]

                    from.value.amount == 50.0//from L:49
                            to.value.amount == 60.0 &&
                            fee.value.amount ==5.0 &&
                            fee.type == TransactionType.Expense
                }

            )
        }
    }
}
//step 7: Comment class WriteTransferAct L:97-104 in add fee function
//for a mutation test, result: Index 2 out of bounds for length 2
