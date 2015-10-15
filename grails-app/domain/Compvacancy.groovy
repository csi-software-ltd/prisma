class Compvacancy {
  static mapping = { version false }

  Integer id
  Integer company_id
  Integer composition_id
  Integer salary
  Integer numbers

  Compvacancy setData(_request){
    composition_id = _request.composition_id
    salary = _request.salary
    numbers = _request.numbers
    this
  }

}