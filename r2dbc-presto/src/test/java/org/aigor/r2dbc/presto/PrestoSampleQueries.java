package org.aigor.r2dbc.presto;

interface PrestoSampleQueries {

    String HELLO_QUERY =
        "select 1 as day, 'November' as month, 'Kyiv' as city, 'Devoxx Ukraine' as event";

    String TPC_H_QUERY_5 =
        "SELECT\n" +
            "    n.name,\n" +
            "    sum(l.extendedprice * (1 - l.discount)) as revenue\n" +
            "FROM\n" +
            "    customer c,\n" +
            "    orders o,\n" +
            "    lineitem l,\n" +
            "    supplier s,\n" +
            "    nation n,\n" +
            "    region r\n" +
            "WHERE\n" +
            "    c.custkey = o.custkey\n" +
            "    AND l.orderkey = o.orderkey\n" +
            "    AND l.suppkey = s.suppkey\n" +
            "    AND c.nationkey = s.nationkey\n" +
            "    AND s.nationkey = n.nationkey\n" +
            "    AND n.regionkey = r.regionkey\n" +
            "    AND r.name = 'ASIA'\n" +
            "    AND o.orderdate >= date '1994-01-01'\n" +
            "    AND o.orderdate < date '1994-01-01' + interval '1' year\n" +
            "GROUP BY\n" +
            "    n.name\n" +
            "ORDER BY\n" +
            "    revenue desc";

    String TPC_H_QUERY_10 = "SELECT\n" +
        "    c.custkey,\n" +
        "    c.name,\n" +
        "    sum(l.extendedprice * (1 - l.discount)) as revenue,\n" +
        "    c.acctbal,\n" +
        "    n.name,\n" +
        "    c.address,\n" +
        "    c.phone,\n" +
        "    c.comment\n" +
        "FROM\n" +
        "    customer c,\n" +
        "    orders o,\n" +
        "    lineitem l,\n" +
        "    nation n\n" +
        "WHERE\n" +
        "    c.custkey = o.custkey\n" +
        "    AND l.orderkey = o.orderkey\n" +
        "    AND o.orderdate >= date '1993-10-01'\n" +
        "    AND o.orderdate < date '1993-10-01' + interval '3' month\n" +
        "    AND l.returnflag = 'R'\n" +
        "    AND c.nationkey = n.nationkey\n" +
        "GROUP BY\n" +
        "    c.custkey,\n" +
        "    c.name,\n" +
        "    c.acctbal,\n" +
        "    c.phone,\n" +
        "    n.name,\n" +
        "    c.address,\n" +
        "    c.comment\n" +
        "ORDER BY\n" +
        "    revenue desc\n" +
        "LIMIT 100";
}