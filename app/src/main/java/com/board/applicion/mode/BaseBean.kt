package com.board.applicion.mode

import io.objectbox.annotation.Transient

class BaseBean {

    @Transient
    var isToDelete = false
}