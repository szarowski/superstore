'use strict';

const rootPath = '/store/products/';
const attributes = {id: 'Identifier',
        name: 'Product Name',
        description: 'Product Description',
        prices: {
            USD: 'Price in USD',
            GBP: 'Price in GBP'
        }
    };
const sortByKeyLength = (o1, o2) => {
	return o1.key.length > o2.key.length ? 1 : o1.key.length === o2.key.length ? 0 : -1;
};

function flatten(arr) {
	return arr.reduce((flat, toFlatten) => {
		return flat.concat(
			toFlatten === 'prices' ? flatten(toFlatten) : toFlatten);
	}, []);
}

function handleSubmitInternal(e, refs, attributes, prices, filtered) {
	e.preventDefault();
	var product = {};
	Object.keys(attributes).filter(attribute => {
		return attribute !== filtered;
	}).forEach(attribute => {
		if (attribute === 'prices') {
			var pricesObj = {};
			Object.keys(attributes[attribute]).forEach(price => {
				pricesObj[price] = ReactDOM.findDOMNode(refs[price]).value.trim();
			});
			product[attribute] = pricesObj;
		} else {
			product[attribute] = ReactDOM.findDOMNode(refs[attribute]).value.trim();
		}
	});
	return product;
}

class App extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
			products: [],
			attributes: attributes
		};
		this.onCreate = this.onCreate.bind(this);
		this.onUpdate = this.onUpdate.bind(this);
		this.onDelete = this.onDelete.bind(this);
	}

	loadFromServer() {
		$.ajax({
            type: 'GET',
            url: rootPath,
			dataType: 'json',
			headers: {'Accept': 'application/hal+json',
				contentType: 'application/hal+json; charset=utf-8'},
			cache: false,
            success: (products => {
				this.setState({products: products});
            }).bind(this),
			error: (response => {
				alert(JSON.parse(response.responseText).fieldErrors.reduce((messages, error) => {
					return messages + 'Field: ' + error.field + ' - ' + error.message + '\n';
				}, ['GET Errors:\n']));
			}).bind(this)
        });
    }

	onCreate(newProduct) {
		$.ajax({
			type: 'POST',
			url: rootPath,
			dataType: 'json',
			contentType: 'application/hal+json; charset=utf-8',
			cache: false,
			data: JSON.stringify(newProduct),
			success: (() => {
				this.loadFromServer();
			}).bind(this),
			error: (response => {
				alert(JSON.parse(response.responseText).fieldErrors.reduce((messages, error) => {
					return messages + 'Field: ' + error.field + ' - ' + error.message + '\n';
				}, ['POST Errors:\n']));
			}).bind(this)
        });
	}

	onUpdate(product, updatedProduct) {
		$.ajax({
			type: 'PUT',
			url: rootPath + product.id,
			dataType: 'json',
			contentType: 'application/hal+json; charset=utf-8',
			cache: false,
			data: JSON.stringify(updatedProduct),
            success: (() => {
				this.loadFromServer();
            }).bind(this),
			error: (response => {
				alert(JSON.parse(response.responseText).fieldErrors.reduce((messages, error) => {
					return messages + 'Field: ' + error.field + ' - ' + error.message + '\n';
				}, ['PUT Errors:\n']));
			}).bind(this)
		});
	}

	onDelete(idObj) {
		$.ajax({
            type: 'DELETE',
            url: rootPath + idObj[0].id,
			dataType: 'json',
			contentType: 'application/hal+json; charset=utf-8',
			cache: false,
            success: (() => {
                this.loadFromServer();
            }).bind(this),
			error: (response => {
				alert(JSON.parse(response.responseText).fieldErrors.reduce((messages, error) => {
					return messages + 'Field: ' + error.field + ' - ' + error.message + '\n';
				}, ['DELETE Errors:\n']));
			}).bind(this)
		});
    }

	render() {
		return (
			<div>
				<CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
				<ProductList attributes={this.state.attributes}
		                     products={this.state.products}
		                     onUpdate={this.onUpdate}
							 onDelete={this.onDelete}/>
			</div>
		)
	}
}

class CreateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		var refs = this.refs;
		var attributes = this.props.attributes;
		var prices = this.props.attributes.prices;
		var newProduct = handleSubmitInternal(e, refs, attributes, prices, 'id');
		this.props.onCreate(newProduct);
		Object.keys(attributes).filter(attribute => {
			return attribute !== 'id'
		}).forEach(attribute => {
			if (attribute === 'prices') {
				Object.keys(prices).forEach(price => {
                    ReactDOM.findDOMNode(refs[price]).value = ''; // clear out the dialog's inputs
                })
            } else {
                ReactDOM.findDOMNode(refs[attribute]).value = ''; // clear out the dialog's inputs
            }
		});
		window.location = "#";
	}
	
	render() {
		var attributes = this.props.attributes;
        var placeholders = flatten(Object.keys(attributes).map(attribute => {
			if (attribute === 'prices') {
                return Object.keys(attributes.prices).map(price => {
                    return {key : price, value: attributes.prices[price]};
                });
            } else {
                return {key : attribute, value: attributes[attribute]};
            }
        }));
        
        var inputs = placeholders.filter(placeholder => {
			return placeholder.key !== 'id'
		}).map(placeholder =>
            <p key={placeholder.key}>
                <input type="text" placeholder={placeholder.value} ref={placeholder.key} className="field" />
            </p>
        );
		
		return (
			<div>
				<a href="#createProduct">Create</a>

				<div id="createProduct" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new product</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}

class UpdateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var refs = this.refs;
		var attributes = this.props.attributes;
		var prices = this.props.attributes.prices;
		var updatedProduct = handleSubmitInternal(e, refs, attributes, prices);
		this.props.onUpdate(this.props.product, updatedProduct);
		window.location = "#";
	}

	render() {
		var attributes = this.props.attributes;
		var placeholders = flatten(Object.keys(attributes).map(attribute => {
			if (attribute === 'prices') {
				return Object.keys(attributes.prices).map(price => {
					return {key : price, value: attributes.prices[price]};
				});
			} else {
				return {key : attribute, value: attributes[attribute]};
			}
		}));

		var defaultValues = {};
		this.props.product.sort(sortByKeyLength).forEach(obj => {
			defaultValues[obj.key] = obj.value;
		});

		var idPlaceholder = placeholders.find(placeholder => {
			return placeholder.key === 'id';
		});
		var inputs = [
			<p key={idPlaceholder.key}>
				{idPlaceholder.value}
				<input type="text" placeholder={idPlaceholder.value} defaultValue={defaultValues[idPlaceholder.key]}
					ref={idPlaceholder.key} className="field" readOnly/>
			</p>].concat(placeholders.filter(placeholder => {
				return placeholder.key !== 'id'
			}).map(placeholder =>
			<p key={placeholder.key}>
				{placeholder.value}
				<input type="text" placeholder={placeholder.value} defaultValue={defaultValues[placeholder.key]}
					   ref={placeholder.key} className="field"/>
			</p>
		));

		var dialogId = "updateProduct-" + this.props.product.id;

		return (
			<div>
				<a href={"#" + dialogId}>Update</a>
				<div id={dialogId} className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Update an product</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Update</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}

class ProductList extends React.Component {

	constructor(props) {
		super(props);
	}
    
	render() {
		var attributes = this.props.attributes;
		var placeholders = flatten(Object.keys(attributes).map(attribute => {
			if (attribute === 'prices') {
				var prices = [];
				Object.keys(attributes.prices).map(price => {
					prices.push({key : price, value: attributes.prices[price]});
				});
				return prices;
			} else {
				return {key : attribute, value: attributes[attribute]};
			}
		})).sort(sortByKeyLength).map(product =>
			<th key={product.key}>{product.key}</th>
		);

		var propProducts = this.props.products;
		var values = propProducts.map(product => {
			var obj = product;
			return Object.keys(obj).reduce((rows, key) => {
				if (key === 'prices') {
					return rows.concat(Object.keys(obj.prices).map(price => {
						return ({id: obj['id'], key: price, value: obj.prices[price]});
					}));
				}
				return rows.concat([{id: obj['id'], key: key, value: obj[key]}]);
			}, []);
		});
		
		var products = values.map((line, index) =>
			<Product key={line[index].id}
					 attributes={attributes}
					 line={line}
					 onUpdate={this.props.onUpdate}
					 onDelete={this.props.onDelete}/>
		);
		return (
			<div>
				<table>
				    <thead>
                    	<tr>
							{placeholders}
                        	<th/>
                    	</tr>
					</thead>
					<tbody>
						{products}
					</tbody>
				</table>
			</div>
		)
	}
}

class Product extends React.Component {

	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.line.filter(obj => {
			return obj.key === 'id';
		}));
	}

	render() {
		var line = this.props.line;
		var values = line.sort(sortByKeyLength).map(obj =>
			<td key={obj.key}>{obj.value}</td>
		);
		
		return (
			<tr>
				{values}
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
				<td>
					<UpdateDialog product={this.props.line}
								  attributes={this.props.attributes}
								  onUpdate={this.props.onUpdate}/>
				</td>
			</tr>
		)
	}
}

ReactDOM.render(
  <App />,
  document.getElementById('react')
);