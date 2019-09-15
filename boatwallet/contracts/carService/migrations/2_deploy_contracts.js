var Contract = artifacts.require("./carService");

module.exports = function(deployer) {
  deployer.deploy(Contract);
};
