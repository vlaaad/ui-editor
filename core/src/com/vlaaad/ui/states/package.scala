package com.vlaaad.ui

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.Actor

/** Created 07.06.14 by vlaaad */
package object states {
  type GdxArray[A] = com.badlogic.gdx.utils.Array[A]

  implicit def function2ChangeListener(f: () => Unit): ChangeListener = new ChangeListener {
    override def changed(event: ChangeEvent, actor: Actor): Unit = f()
  }

  implicit def iterator2array[A](iterable: Iterable[A]): GdxArray[A] = {
    val r = new GdxArray[A]
    iterable.foreach(v => r.add(v))
    r
  }

  implicit def function2runnable(f: () => Unit): Runnable = new Runnable {
    override def run(): Unit = f()
  }
}
