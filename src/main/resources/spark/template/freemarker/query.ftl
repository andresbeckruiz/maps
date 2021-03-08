<#assign content>
<h1> STARS </h1>
  <div id="page">
  <div id="neighbors">
    <h2 id="neighborsTag"> NEIGHBORS </h2>
<p Find nearest Neighbors! <br> <h3 id="args"> Use format [positive integer "name" or positive integer double double double] </h3>
     Ex: 1 0 0 0
    <br> Ex: 1 "Sol"
    <form id="form" method="POST" action="/neighbors">
      <br>
        <div class="form-check form-switch">
            <label id="labelclass" class="form-check-label" for="neighbors_switched"> Toggle on for the neighbors method and off for naive neighbors </label>
            <input id="forminput" class="form-check-input" type="checkbox" name="neighbors_switched" id="neighbors_switched" checked>
        </div>
        <br>
             <label id="label" for="text"> Enter command! </label><br>
            <textarea id="area" name="text" id="text"></textarea>
            <input id="input" type="submit">
</form>
</p>
  </div>
  <div id="radius">
    <h2 id="radiusTag"> RADIUS </h2>
<p Find Stars within a radius! <br> <h3 id="args2"> Use format [positive integer "name" or positive integer double double double] </h3>
     Ex: 1 0 0 0
    <br> Ex: 1 "Sol"
    <form method="POST" action="/radius">
      <br>
        <div class="form-check form-switch">
            <label id="togglelabel" class="form-check-label" for="radius_switched"> Toggle on for the radius method and off for naive radius</label>
            <input id="inputlabel" class="form-check-input" type="checkbox" name="radius_switched" id="radius_switched" checked>
        </div>
        <br>
  <label id="label2" for="text2"> Enter command!:</label><br>
  <textarea id="area2" name="text2" id="text2"></textarea>
  <input id="submit2" type="submit">
</form>
</p>
  </div>
    </div>
</#assign>
<#include "main.ftl">
