<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="leaflet.css">
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
        integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
        crossorigin=""/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
          integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
          crossorigin=""></script>

    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyADAJ0ArZEIKttCc6530AInhzjyH5JDMgY&libraries=places"></script>
    <script src="jquery.geocomplete.js"></script>

    <script src="leaflet.js"></script>
</head>
<body>
<form id="form">
  From:<br>
  <input type="text" name="from" id="from"><br>
    To:<br>
    <input type="text" name="to" id="to"><br>
  <br>
  <button type="submit" id="submit" disabled>Search</button>
</form>
<br>
<div id="mapid"></div>
<script>
  debugger;
    var url = "http://localhost:9999/initialize";
    $.get(url, function( data ) {
        initialiseMap();
        $('#submit').removeAttr('disabled');
        $("#form").submit(function(event) {
            debugger;
            event.preventDefault();
            getCoordinates();
        });
    });
</script>
</body>
</html>