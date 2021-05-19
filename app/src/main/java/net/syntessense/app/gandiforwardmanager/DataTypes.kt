package net.syntessense.app.gandiforwardmanager

class Domain (var fqdn: String, var href: String)

class Target(var address : String, var selected : Boolean)

class Address (var source: String, var destinations : ArrayList<String>, var href : String) {

/*
    companion object {
        fun parseAddresses(addresses : String) : ArrayList<String> {
            var strs = addresses.split("\n")
            var adds = ArrayList<String>()
            for (i in strs)
                adds.add(i.trim().lowercase())
            return adds;
        }
    }

    static public ArrayList<ForwardAddress> parseFAddresses (String addresses ) {
        String[] strs = addresses.split("\n");
        String[] strss;
        ArrayList<ForwardAddress> arr = new ArrayList<>();
        for ( int i = 0; i < strs.length; i++ ) {
            strss = strs[i].trim().toLowerCase().split(":");
            arr.add(new ForwardAddress(strss[0], strss.length > 1 && strss[1].charAt(0) == '1'));
        }
        return arr;
    }
*/

}