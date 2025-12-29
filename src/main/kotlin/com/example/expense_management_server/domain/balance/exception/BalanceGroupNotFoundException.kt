package com.example.expense_management_server.domain.balance.exception

import java.util.UUID

class BalanceGroupNotFoundException(balanceGroupId: UUID) : RuntimeException("Balance group $balanceGroupId not found")