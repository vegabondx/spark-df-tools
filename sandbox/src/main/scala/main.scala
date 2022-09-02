package com.github.vegabondx.sandbox

object Main {
  def isPalindrome(s:String):Boolean = {
    s.toLowerCase.reverse == s.toLowerCase
  }
}