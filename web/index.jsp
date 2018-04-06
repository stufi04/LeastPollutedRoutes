<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="leaflet.css">
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
        integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
        crossorigin=""/>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
          integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
          crossorigin=""></script>

    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyADAJ0ArZEIKttCc6530AInhzjyH5JDMgY&libraries=places"></script>
    <script src="jquery.geocomplete.js"></script>

    <script src="leaflet.js"></script>
    <script src="leaflet-heat.js"></script>
</head>
<body>

  <ul class="nav nav-tabs">
    <li class="active"><a data-toggle="tab" href="#lpr">Least polluted routes</a></li>
    <li><a data-toggle="tab" href="#asp">AirSpeck positions</a></li>
  </ul>

  <div class="tab-content">
    <div id="lpr" class="tab-pane fade in active">
      <form id="form">

          <div class="form-group row">
              <div class="col-sm-1">
                  <label class="control-label">From:</label>
              </div>

            <div class="col-sm-12">
              <input type="text" name="from" id="from"><br>
            </div>
          </div>

          <div class="form-group row">
              <div class="col-sm-1">
                <label class="control-label">To:</label>
              </div>

              <div class="col-sm-12">
                  <input type="text" name="to" id="to"><br>
              </div>
          </div>

          <div>
              <input type="checkbox" id="shortest">
              <label for="shortest">Show shortest path</label>
          </div>
          <br>

          <div class="form-group row">
              <div class="col-sm-12">
                  <button type="submit" id="submit" class="btn btn-default" disabled>Search</button>
              </div>
          </div>

      </form>
    </div>

    <div id="asp" class="tab-pane fade">

        <form id="homesForm" method="post" class="form-horizontal">

            <div class="form-group row">

                <div class="col-sm-1">
                    <label class="control-label">No. of Airspecks</label>
                </div>

                <div>
                    <input type="text" class="col-sm-5 form-control airspecks" id="airspecks"/>
                </div>

            </div>

            <div class="form-group row">

                <div class="col-sm-1">
                    <label class="control-label">Student home</label>
                </div>

                <div>
                    <input type="text" class="col-sm-5 form-control student-home" name="option[]" />
                </div>

                <div class="col-sm-1">
                    <button type="button" class="btn btn-default addButton">+</button>
                </div>

            </div>

            <!-- The option field template containing an option field and a Remove button -->
            <div class="form-group row hide" id="optionTemplate">

                <div class="col-sm-1">
                    <label class="control-label">Student home</label>
                </div>

                <div>
                    <input type="text" class="col-sm-5 form-control student-home" name="option[]" />
                </div>

                <div class="col-sm-1">
                    <button type="button" class="btn btn-default removeButton">-</button>
                </div>

            </div>

            <div class="form-group">
                <div class="col-sm-offset-1">
                    <button type="submit" class="btn btn-default" id="submit-homes">Submit</button>
                </div>
            </div>

        </form>

        <script>
            $(document).ready(function() {
                // The maximum number of options
                var MAX_OPTIONS = 10;
                var fields = 1;

                $('#homesForm')

                    // Add button click handler
                    .on('click', '.addButton', function() {
                        debugger;
                        var $template = $('#optionTemplate'),
                            $clone    = $template
                                .clone()
                                .removeClass('hide')
                                .removeAttr('id')
                                .insertBefore($template),
                            $option   = $clone.find('[name="option[]"]');
                         $option.geocomplete();

                        debugger;
                        fields++;
                        if (fields >= MAX_OPTIONS) {
                            $('#homesForm').find('.addButton').attr('disabled', 'disabled');
                        }
                    })

                    // Remove button click handler
                    .on('click', '.removeButton', function() {
                        var $row    = $(this).parents('.form-group'),
                            $option = $row.find('[name="option[]"]');

                        $row.remove();

                        fields--;
                        if (fields < MAX_OPTIONS) {
                            $('#homesForm').find('.addButton').removeAttr('disabled');
                        }
                    })
            });
        </script>
    </div>

    <br>
    <div id="mapid"></div>
    <script>
        debugger;
        $('.nav-tabs a').on('shown.bs.tab', function(event){
            clearMap();
        });

        var pollutionGridUrl = "https://b42ba416.ngrok.io/kriging_full";
        $.get(pollutionGridUrl, function(pollutionGrid) {
            var url = "http://localhost:9999/initialize";
            //pollutionGrid = null;
            $.post(url, {pollutionGrid: JSON.stringify(pollutionGrid)}, function( data ) {
                debugger;
                initialiseMap();
                //initialiseHeatmap(data);
                $('#submit').removeAttr('disabled');
                $("#form").submit(function(event) {
                    event.preventDefault();
                    getCoordinates();
                });
                $("#homesForm").submit(function(event) {
                    event.preventDefault();
                    getHomeCoordinates();
                });
            });
        });
    </script>

  </div>

</body>
</html>