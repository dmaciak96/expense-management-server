package com.example.expense_management_server.adapter.persistence.repository

import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExpenseRepository : JpaRepository<ExpenseEntity, UUID>