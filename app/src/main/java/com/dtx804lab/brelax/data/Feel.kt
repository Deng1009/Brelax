package com.dtx804lab.brelax.data

import com.dtx804lab.brelax.R

enum class Feel(override val textID: Int, override val imageID: Int): ImageData {

    HAPPY(R.string.feel_happy, R.drawable.ic_missing_image),
    RELAX(R.string.feel_relax, R.drawable.ic_missing_image),
    COOL(R.string.feel_cool, R.drawable.ic_missing_image),
    FERVENT(R.string.feel_fervent, R.drawable.ic_missing_image),
    LUCKY(R.string.feel_lucky, R.drawable.ic_missing_image),
    GRATEFUL(R.string.feel_grateful, R.drawable.ic_missing_image),
    MAD(R.string.feel_mad, R.drawable.ic_missing_image),
    FEAR(R.string.feel_fear, R.drawable.ic_missing_image),
    ANXIETY(R.string.feel_anxiety, R.drawable.ic_missing_image),
    AWKWARD(R.string.feel_awkward, R.drawable.ic_missing_image),
    UPSET(R.string.feel_upset, R.drawable.ic_missing_image),
    SAD(R.string.feel_sad, R.drawable.ic_missing_image)

}