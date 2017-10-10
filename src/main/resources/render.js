var margin = {top: 20, right: 150, bottom: 30, left: 50},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x = d3.scaleLinear().range([0, width]);
var y = d3.scaleLinear().range([height, 0]);

var svg = d3.select("svg")
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
    .attr("class", "histogram result1")
    .attr("d", d3.line()
                 .curve(d3.curveStep)
                 .x(function(d) { return x(d[0]); })
                 .y(function(d) { return y(d[1]); })
             );

    //Add Result2
    svg.append("path")
      .datum(result2)
      .attr("class", "histogram result2")
      .attr("d", d3.line()
                   .curve(d3.curveStep)
                   .x(function(d) { return x(d[0]); })
                   .y(function(d) { return y(d[1]); })
               );

  //Add Mean 1
  svg.append("svg:line")
      .attr("class", "mean result1")
      .attr("x1", x(mean1))
      .attr("y1", height)
      .attr("x2", x(mean1))
      .attr("y2", 0);

  svg.append("svg:rect")
      .attr("class", "ci1")
      .attr("x", x(lowerCI1))
      .attr("y", 0)
      .attr("width", x(upperCI1)-x(lowerCI1))
      .attr("height", height);

    //Add Mean 2
    svg.append("svg:line")
        .attr("class", "mean result2")
        .attr("x1", x(mean2))
        .attr("y1", height)
        .attr("x2", x(mean2))
        .attr("y2", 0);

  svg.append("svg:rect")
      .attr("class", "ci2")
      .attr("x", x(lowerCI2))
      .attr("y", 0)
      .attr("width", x(upperCI2)-x(lowerCI2))
      .attr("height", height);

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

var tableFormat = d3.format(".5s");
d3.select("#mean1").text(tableFormat(mean1));
d3.select("#lowerCI1").text(tableFormat(lowerCI1));
d3.select("#upperCI1").text(tableFormat(upperCI1));
d3.select("#mean2").text(tableFormat(mean2));
d3.select("#lowerCI2").text(tableFormat(lowerCI2));
d3.select("#upperCI2").text(tableFormat(upperCI2));
d3.select("#difflowerCI").text(tableFormat(difflowerCI));
d3.select("#diffupperCI").text(tableFormat(diffupperCI));

var ciFormat = d3.format(".3s");
var confidence = ciFormat((1-alpha)*100);
d3.select("#alpha").text("( "+confidence+"% confidence )");

var pctFormat = d3.format(".2f");
if (difflowerCI<0 && diffupperCI>0)
    d3.select("#analysis").text("As this range contains 0, these result sets can be said to have the same mean with "+confidence+"% confidence");
else {
    d3.select("#analysis").text("These result sets can be said to have different means, with "+confidence+"% confidence they are not the same");
    if (diffupperCI<0) {
        d3.select("#direction").text("less");
        d3.select("#lowerfactor").text(pctFormat(-diffupperCI*100.0/mean1)+"%");
        d3.select("#upperfactor").text(pctFormat(-difflowerCI*100.0/mean1)+"%");
    }
    else {
        d3.select("#direction").text("greater");
        d3.select("#lowerfactor").text(pctFormat(difflowerCI*100.0/mean1)+"%");
        d3.select("#upperfactor").text(pctFormat(diffupperCI*100.0/mean1)+"%");
    }
    d3.select("#subanalysis").style("visibility","visible");
}