package com.example.payandgo

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
    .serverUrl("http://localhost:5000/graphql")
    .build()