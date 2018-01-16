/**
 * Created by st.ivanov44 on 23/10/2017.
 */

var mymap;
var marker1 = null;
var marker2 = null;

function initialiseMap() {

    debugger;
    $("#from").geocomplete();
    $("#to").geocomplete();
    $(".student-home").geocomplete();
    mymap = L.map('mapid').setView([55.944, -3.196], 14.5);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1Ijoic3R1ZmkwNCIsImEiOiJjajkxaWJwNGEycDllMnduNzZhMnFjano5In0.qv76WYiQkevWL8pDELujXQ'
    }).addTo(mymap);

}

function initialiseHeatmap(data) {

    var values = data.match(/[^\s]+/g);
    var points = [];
    for (var i = 0; i < values.length; i+=3) {
        points.push([values[i], values[i+1], values[i+2]]);
    }
    debugger;
    var heat = L.heatLayer(points, {radius: 10, blur: 15, max: 30.0, gradient: {0.25: 'green', 0.5: 'lime', 0.75: 'yellow', 1.0: 'red'}})
        .addTo(mymap);

}

//  function getPollutionFromFile() {
//
//      var allText = null;
//
//      var rawFile = new XMLHttpRequest();
//      rawFile.open("GET", 'WEB-INF/data/polluted_nodes1.txt', false);
//     rawFile.onreadystatechange = function ()
//     {
//         if(rawFile.readyState === 4)
//         {
//             if(rawFile.status === 200 || rawFile.status == 0)
//             {
//                 allText = rawFile.responseText;
//             }
//         }
//     }
//     rawFile.send(null);
//
//     var values = allText.match(/[^\s]+/g);
//     var nodes = [];
//     for (var i = 0; i < values.length; i+=3) {
//         nodes.push([values[i], values[i+1], values[i+2]]);
//     }
//     return nodes;
//
// }


function getCoordinates() {

    var addressFrom =  $('#from').val();
    var addressTo = $('#to').val();

    var urlFrom = "https://maps.googleapis.com/maps/api/geocode/json?address=" + addressFrom + "&key=AIzaSyADAJ0ArZEIKttCc6530AInhzjyH5JDMgY";
    var urlTo = "https://maps.googleapis.com/maps/api/geocode/json?address=" + addressTo + "&key=AIzaSyADAJ0ArZEIKttCc6530AInhzjyH5JDMgY";

    var fromLat, fromLng, toLat, toLng;

    $.get(urlFrom, function( data ) {
        fromLat = data.results[0].geometry.location.lat;
        fromLng = data.results[0].geometry.location.lng;
        $.get(urlTo, function( data ) {
            debugger;
            toLat = data.results[0].geometry.location.lat;
            toLng = data.results[0].geometry.location.lng;
            getRoute(fromLat, fromLng, toLat, toLng);
        });
    });
    
}

function getHomeCoordinates() {

    var list = [];
    var homes = $('.student-home').length - 1;
    var num = 0;

    $('.student-home').each(function(i, obj) {
        var address = $(this).val();
        var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyADAJ0ArZEIKttCc6530AInhzjyH5JDMgY";
        $.get(url, function( data ) {
            num++;
            var lat = data.results[0].geometry.location.lat;
            var lng = data.results[0].geometry.location.lng;
            list.push(lat);
            list.push(lng);
            if (num == homes) {
                getRoutesToUniversity(list);
            }
        });
    });
}

function getRoute(lat1, lng1, lat2, lng2) {

    var url = "http://localhost:9999/getroute";
    $.post(url, {lat1: lat1, lng1: lng1, lat2: lat2, lng2: lng2}, function( data ) {
        clearMap();
        var values = data.match(/[^\s]+/g);
        var route = [];
        for (var i = 0; i < values.length; i+=2) {
            route.push([values[i], values[i+1]]);
        }
        L.polyline(route, {color: 'blue'}).addTo(mymap);
        console.log(route);
        debugger;
        marker1 = L.marker(route[0]);
        marker1.addTo(mymap);
        marker2 = L.marker(route[route.length-1]);
        marker2.addTo(mymap);
    });
}

function getRoutesToUniversity(list) {

    var url = "http://localhost:9999/getroutestouni";
    var route;
    $.post(url, {list: JSON.stringify(list)}, function(data) {
        clearMap();
        debugger;
        var dividedByHomes = data.split('@');
        for (var i = 0; i < dividedByHomes.length; i++) {
            var values = dividedByHomes[i].match(/[^\s]+/g);
            route = [];
            for (var j = 0; j < values.length; j+=2) {
                route.push([values[j], values[j+1]]);
            }
            L.polyline(route, {color: 'blue'}).addTo(mymap);
            marker1 = L.marker(route[0]);
            marker1.addTo(mymap);
            marker2 = L.marker(route[route.length-1]);
            marker2.addTo(mymap);
        }
        debugger;
    });

}

function clearMap() {
    for(i in mymap._layers) {
        if(mymap._layers[i]._path != undefined) {
            try {
                mymap.removeLayer(mymap._layers[i]);
            }
            catch(e) {
                console.log("problem with " + e + mymap._layers[i]);
            }
        }
    }
    if (marker1 != null) mymap.removeLayer(marker1);
    if (marker2 != null) mymap.removeLayer(marker2);
}
