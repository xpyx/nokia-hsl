/**
 * Description: Data Class for Here traffic model
 *
 * Basics done with http://www.json2kotlin.com but altered a lot
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Event (
	@SerializedName("EVENT_ITEM_CANCELLED") val eventItemCancelled: Boolean? = null,
	@SerializedName("SCHEDULED_CONSTRUCTION_EVENT") val eventScheduledConstructionEvent: ScheduledConstructionEvent? = null
)