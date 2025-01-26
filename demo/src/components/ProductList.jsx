import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ProductList = () => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/products');
                setProducts(response.data);
            } catch (error) {
                console.error('Error fetching products:', error);
            }
        };
        
        fetchProducts();
    }, []);

    return (
        <div className="product-list">
            <h2>商品列表</h2>
            <div className="products-grid">
                {products.map(product => (
                    <div key={product.id} className="product-card">
                        <img src={product.imageUrl} alt={product.name} />
                        <h3>{product.name}</h3>
                        <p>{product.description}</p>
                        <p className="price">￥{product.price}</p>
                        <button>加入购物车</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProductList; 