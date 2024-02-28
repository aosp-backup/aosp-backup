package com.stevesoltys.aosp_backup.manager.backup.restore.plan

import com.stevesoltys.aosp_backup.manager.backup.restore.plan.saf.SAFRestorePlan
import javax.inject.Inject
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
class RestorePlanManager @Inject constructor(
  private val safRestorePlan: SAFRestorePlan
) {

  fun restorePlanList(): List<RestorePlan> = listOf(safRestorePlan)
}
