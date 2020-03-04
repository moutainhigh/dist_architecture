/**
 * 当前包内负责执行各种账务处理的逻辑计算，有多种Processor处理器，有以下几点说明
 * 1、当前包内的逻辑处理只负责对账户金额进行加、减，不考虑账务并发时的安全性，需由调用方自行保障
 */
package com.xpay.service.accountsub.accounting;