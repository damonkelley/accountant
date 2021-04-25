package com.damonkelley.accountant.account.domain

import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot

class Account(val aggregateRoot: WritableAggregateRoot<T>): WritableAggregateRoot<Unit> by aggregateRoot