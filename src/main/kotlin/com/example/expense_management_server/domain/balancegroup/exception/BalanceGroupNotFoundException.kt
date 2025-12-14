package com.example.expense_management_server.domain.balancegroup.exception

import java.util.UUID

class BalanceGroupNotFoundException(balanceGroupId: UUID) : RuntimeException("Balance group $balanceGroupId not found")