class Expense2dep {
  static mapping = { version false }

  Integer id
  Integer expensetype_id
  Integer department_id

  def csiSetExpense2dep(hsInrequest){
    expensetype_id=hsInrequest?.expensetype_id?:0
    department_id=hsInrequest?.department_id?:0
    this
  }
}