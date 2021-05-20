package net.syntessense.app.gandiforwardmanager

class Domain (var fqdn: String, var href: String)

class Target(var address : String, var selected : Boolean) {
    companion object {
        fun parseList(str : String) : ArrayList<Target> {
            val mainTargets = ArrayList<Target>()
            var tgt : List<String>
            for ( mf in str.split("\n") ) {
                if (mf.trim() != "") {
                    tgt = mf.split(":")
                    mainTargets.add(
                        Target(
                            tgt[0],
                            tgt.size > 1 && tgt[1] == "1"
                        )
                    )
                }
            }
            return mainTargets
        }
    }
}

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