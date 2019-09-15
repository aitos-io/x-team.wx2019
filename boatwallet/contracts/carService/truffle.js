module.exports = {
  networks: {
    development: {
      host: "192.168.56.1",
      port: 7545,
      network_id: '5777',
      gas: 300000,
      gasPrice: 20000000000,
    },

    juice: {
      host: "34.221.228.64",
      port: 6789,
      network_id: '*',
      gas: 300000,
      gasPrice: 20000000000,
    },

    juice_demo: {
      host: "2i460k0320.zicp.vip",
      port: 44279,
      network_id: '*',
      gas: 300000,
      gasPrice: 20000000000,
    },

    aitos: {
      host: "47.104.142.169",
      port: 7545,
      network_id: '5777',
      gas: 3000000,
      gasPrice: 20000000000,
    },
  },
};
