var margin = {top: 20, right: 150, bottom: 30, left: 50},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x = d3.scaleLinear().range([0, width]);
var y = d3.scaleLinear().range([height, 0]);

var svg = d3.select("body").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");




  x.domain(d3.extent(result1.concat(result2), function(d) { return d[0]; }));
  y.domain(d3.extent(result1.concat(result2), function(d) { return d[1]; }));

    var xFormatValue = d3.format(".3s");
    // Add the X Axis
    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x).tickFormat(function(d) { return xFormatValue(d)}));

    // Add the Y Axis
    svg.append("g")
        .attr("class", "axis")
        .call(d3.axisLeft(y));

  //Add Result1
  svg.append("path")
    .datum(result1)
    .attr("class", "result1")
    .attr("d", d3.line()
                 .curve(d3.curveStep)
                 .x(function(d) { return x(d[0]); })
                 .y(function(d) { return y(d[1]); })
             );

    //Add Result2
    svg.append("path")
      .datum(result2)
      .attr("class", "result2")
      .attr("d", d3.line()
                   .curve(d3.curveStep)
                   .x(function(d) { return x(d[0]); })
                   .y(function(d) { return y(d[1]); })
               );

  //Add Mean 1
  svg.append("svg:line")
      .attr("class", "mean1")
      .attr("x1", x(mean1))
      .attr("y1", height)
      .attr("x2", x(mean1))
      .attr("y2", -10);

    //Add Mean 2
    svg.append("svg:line")
        .attr("class", "mean2")
        .attr("x1", x(mean2))
        .attr("y1", height)
        .attr("x2", x(mean2))
        .attr("y2", -10);

  svg.append("text")
      .attr("class", "x label")
      .attr("text-anchor", "end")
      .attr("x", width)
      .attr("y", height - 6)
      .text(units);

  svg.append("text")
      .attr("class", "y label")
      .attr("text-anchor", "end")
      .attr("y", 6)
      .attr("dy", ".75em")
      .attr("transform", "rotate(-90)")
      .text("count");

