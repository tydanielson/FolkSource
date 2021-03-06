enyo.kind({
  name: "Data",
  kind: "enyo.Control",
  statics: {
    getURL: function () {
      //return "http://folksource.grouplens.org/api/";
      //return "http://innsbruck-umh.cs.umn.edu/api/";
      //return "http://192.168.1.95:8080/FolkSource/";
      //return "http://localhost:8080/FolkSource/";
      return "http://populo.cs.umn.edu/staging/";
    },
    getTileURL: function(string) {
      return "http://ugly-umh.cs.umn.edu/api/" + string;
    },
    getUserName: function (a) {
      var b = new enyo.Ajax({
        contentType: "application/json",
        sync: !0,
        url: Data.getURL() + "user/leaderboard"
      });
      b.response(this, function (a, b) {
        this.users = b.leaderboardEntrys;
      });
      b.go();
      for (var x = 0; x < this.users.length; x++) {
        if (this.users[x].id == a) {
          this.log(a);
          return this.users[x].name;
        }
      }
    },
    getBikeData: function(i, j, age, gender, helmet, assistive, bike, ped) {
      var output = [];
      var spIndex;
      if(age) {
        if(i == 2 || i == 4) {
          if(gender) {
            if(j == 1 || j == 3)
              output.push("male");
            else
              output.push("female");
            if(i <= 3)
              output.push("adult");
            else
              output.push("child");
            if(j <= 2) {
              output.push("cyclist");
              if(helmet)
                output.unshift("helmeted");
            } else {
              output.push("pedestrian");
              if(assistive)
                output.unshift("assisted");
            }
          }
        } else if (i === 3 || i === 5) {
          if(j === 0 || j === 2)
            output.push("male");
          else
            output.push("female");
          if(i <= 3)
            output.push("adult");
          else
            output.push("child");
          if(j <= 1)
            output.push("cyclist");
          else
            output.push("pedestrian");
        }
      } else {
        if(gender) {
          if(j === 0 || j === 2)
            output.push("male");
          else
            output.push("female");

          if(helmet && (i == 2 || i == 4))
            output.unshift("helmeted");
          if(assistive && (i == 2 || i == 4))
            output.unshift("assisted");
          if(j <= 1)
            output.push("cyclist");
          else
            output.push("pedestrian");
        } else {
          if(j <= 1)
            output.push("cyclist");
          else
            output.push("pedestrian");
        }
      }
      console.log();
      return output;
    },
    countAdd: function(inputArray) {
      var countData = LocalStorage.get("countData");
      if(countData === undefined) {
        countData = [];
      }
      countData.unshift(inputArray);
      //countData.push(inputArray);
      LocalStorage.set("countData", countData);

      /*db = window.openDatabase("countData", 1, "Count Data", 10000000);
        db.executeSql("CREATE TABLE IF NOT EXISTS data (id unique, timestamp, data)");
        db.executeSql("INSERT INTO data (id, timestamp data) VALUES ()"):*/
    },
    countRemove: function(index) {
      var countData = LocalStorage.get("countData");
      countData.splice(index, 1);
      LocalStorage.set("countData", countData);
    },
    countGetAtIndex: function(index) {
      var ret = LocalStorage.get("countData");
      if(ret) {
        return ret[index];
      } else {
        return -1;
      }
    },
    countSize: function() {
      return LocalStorage.get("countData").length;
    },
    setLocationData: function(inVar) {
      LocalStorage.set("loc", inVar);
    },
    getLocationData: function() {
      return LocalStorage.get("loc");
    },
    setIsReady: function(ready) {
      LocalStorage.set("ready", ready);
    },
    getIsReady: function() {
      return LocalStorage.get("ready");
    }
  }
});
